package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.local.dao.AppUsageDao
import com.dreamteam.breakloop.data.local.dao.DailyUsageStatsDao
import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import com.dreamteam.breakloop.data.local.mapper.toEntity
import com.dreamteam.breakloop.data.local.mapper.toDomain
import com.dreamteam.breakloop.data.remote.mapper.toDto
import com.dreamteam.breakloop.data.remote.UsageRemoteDataSource
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.DailyUsageStats
import com.dreamteam.breakloop.domain.UsageEventsResult
import com.dreamteam.breakloop.domain.repository.UsageRepository
import com.dreamteam.breakloop.domain.sync.SyncScheduler
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateBaselineUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateFlaggedTimeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class UsageRepositoryImpl (
    val usageEventsDataSource: UsageEventsDataSource,
    val installedAppsDataSource: InstalledAppsDataSource,
    val aggregateUsageEventsUseCase: AggregateUsageEventsUseCase,
    private val appUsageDao: AppUsageDao,
    private val dailyUsageStatsDao: DailyUsageStatsDao,
    private val calculateBaselineUseCase: CalculateBaselineUseCase,
    private val calculateFlaggedTimeUseCase: CalculateFlaggedTimeUseCase,
    private val flaggedAppsRepositoryImpl: FlaggedAppsRepositoryImpl,
    private val remote: UsageRemoteDataSource,
    private val syncScheduler: SyncScheduler
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
                    val labeled = flaggedAppsRepositoryImpl.getFlaggedApps()
                    val timeLabeled = calculateFlaggedTimeUseCase.getFlaggedAppTime(uses, labeled.first())
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
                        isPartial = result.usageEvents.isEmpty() && date != LocalDate.now().toString(),
                        updatedAt = System.currentTimeMillis(),
                        flaggedTimeMs = timeLabeled
                    )
                    dailyUsageStatsDao.saveDailyUsageStats(stats.toEntity())
                    syncScheduler.enqueueUsageSync()
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

    override suspend fun backfillMissingDays(startDate: LocalDate, endDate: LocalDate) {
        var date = startDate
        while (!date.isAfter(endDate)){
            if(dailyUsageStatsDao.getDailyUsageStatsOnce(date.toString()) == null){
                refreshDay(date.toString())
            }
            date = date.plusDays(1)
        }
    }
    override suspend fun getBaselineMs(): Long? {
        val firstDay = dailyUsageStatsDao.getEarliestCompleteDate()
        if (firstDay == null){
            return null
        }
        val firstDateLocalDate = LocalDate.parse(firstDay)
        val lastDay = firstDateLocalDate.plusDays(6).toString()
        val days = dailyUsageStatsDao.getDailyUsageStatsByRangeOnce(firstDay, lastDay)
        val daysMode = days.map { list -> list.toDomain() }
        return calculateBaselineUseCase.calculate(daysMode)

    }

    override suspend fun syncUnsyncedDays() {
        val unsyncedDays = dailyUsageStatsDao.getUnsyncedDailyUsageStats()
        for (day in unsyncedDays) {
            val updatedDay = dailyUsageStatsDao.getDailyUsageStatsOnce(day.date) ?: day
            remote.post(updatedDay.toDto())
            dailyUsageStatsDao.markSynced(updatedDay.date, updatedDay.updatedAt)
        }
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