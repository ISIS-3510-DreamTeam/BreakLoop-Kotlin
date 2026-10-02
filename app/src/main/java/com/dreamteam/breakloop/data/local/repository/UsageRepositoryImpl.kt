package com.dreamteam.breakloop.data.local.repository

import com.dreamteam.breakloop.data.local.dao.AppUsageDao
import com.dreamteam.breakloop.data.local.dao.DailyUsageStatsDao
import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import com.dreamteam.breakloop.data.local.mapper.toEntity
import com.dreamteam.breakloop.data.local.mapper.toDomain
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.DailyUsageStats
import com.dreamteam.breakloop.domain.UsageEventsResult
import com.dreamteam.breakloop.domain.repository.UsageRepository
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class UsageRepositoryImpl (
    val usageEventsDataSource: UsageEventsDataSource,
    val installedAppsDataSource: InstalledAppsDataSource,
    val aggregateUsageEventsUseCase: AggregateUsageEventsUseCase,
    private val appUsageDao: AppUsageDao,
    private val dailyUsageStatsDao: DailyUsageStatsDao
): UsageRepository {
    override suspend fun refreshDay(date: String): Boolean {
        return withContext(Dispatchers.IO) {
            val (rangeStart, rangeEnd) = getDayRangeMs(date)
            val includedPackages = installedAppsDataSource.obtainIncludedPackages()

            when (val result = usageEventsDataSource.obtainEvents(rangeStart, rangeEnd)) {
                UsageEventsResult.NoPermission -> false
                is UsageEventsResult.Success -> {
                    val uses = aggregateUsageEventsUseCase.getScreenTimePerPackage(
                        result.usageEvents,
                        rangeStart,
                        rangeEnd,
                        date,
                        includedPackages
                    )
                    val usesEntity: MutableList<AppUsageEntity> = mutableListOf()
                    var foregroundMsTotal =0L
                    for (use in uses){
                        usesEntity.add(use.toEntity())
                        foregroundMsTotal += use.foregroundMs
                    }
                    appUsageDao.replaceAppUsageForDate(date, usesEntity)
                    val previous = dailyUsageStatsDao.getDailyUsageStatsOnce(date)
                    val stats = DailyUsageStats(
                        date = date,
                        screenTimeMs = foregroundMsTotal,
                        pickups = previous?.pickups?:0,
                        unlocks = previous?.unlocks?:0,
                        isPartial = false, // TODO: comparar con fecha de instalación
                        updatedAt = System.currentTimeMillis()
                    )
                    dailyUsageStatsDao.saveDailyUsageStats(stats.toEntity())
                    true
                }
            }
        }
    }

    override fun observeAppUsage(date: String): Flow<List<AppUsage>> {
        val result = appUsageDao.getAppUsageByDate(date)
        val domainResultFlow = result.map { list -> list.map { it.toDomain() } }
        return domainResultFlow

    }
    override fun observeDailyStats(startDate: String, endDate: String): Flow<List<DailyUsageStats>> {
        val result = dailyUsageStatsDao.getDailyUsageStatsByRange(startDate, endDate)
        val domainResultFlow = result.map { list -> list.map { it.toDomain() } }
        return domainResultFlow
    }
}

private fun getDayRangeMs(dateStr: String): Pair<Long, Long> {
    val localDate = LocalDate.parse(dateStr)
    val zoneId = ZoneId.systemDefault()
    val rangeStart = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
    val rangeEnd = when(localDate){
        LocalDate.now() -> System.currentTimeMillis()
        else -> localDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    }
    return Pair(rangeStart, rangeEnd)
}