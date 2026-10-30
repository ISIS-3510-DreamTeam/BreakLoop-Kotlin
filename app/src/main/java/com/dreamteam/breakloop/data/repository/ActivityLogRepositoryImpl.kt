package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.local.dao.ActivityLogDao
import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import com.dreamteam.breakloop.data.remote.ActivityLogRemoteDataSource
import com.dreamteam.breakloop.data.remote.mapper.toDto
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.ActivitySource
import com.dreamteam.breakloop.domain.repository.ActivityLogRepository
import com.dreamteam.breakloop.domain.sync.SyncScheduler
import java.util.UUID

class ActivityLogRepositoryImpl (
    private val activityLogDao: ActivityLogDao,
    private val remote : ActivityLogRemoteDataSource,
    private val syncScheduler: SyncScheduler
): ActivityLogRepository{
    override suspend fun startLog(
        activityId: String,
        category: ActivityCategory,
        source: ActivitySource,
        context: ContextSnapshot
    ): String {
        val id = UUID.randomUUID().toString()
        activityLogDao.insertActivityLog(
            ActivityLogEntity(
                id = id,
                activityId = activityId,
                source = source,
                startedAt = System.currentTimeMillis(),
                completedAt = null,
                xp = 0,
                timeOfDay = context.timeOfDay,
                weather = context.weather?.name,
                availableMin = context.availableMin,
                category = category,
                interactionId = null,
                placeLabel = null,
                latitude = context.latitude,
                longitude = context.longitude,
                synced = false
            ))
        syncScheduler.enqueueActivityLogSync()

        return id
    }


    override suspend fun completeLog(logId: String, xp: Int) {
        activityLogDao.markActivityAsCompleted(logId, System.currentTimeMillis(), xp)
        syncScheduler.enqueueActivityLogSync()
    }

    override suspend fun getRecentActivityIds(limit: Int): List<String> {
        return activityLogDao.getRecentCompletedActivityIds(limit)
    }

    override suspend fun syncActivityLogs() {
        val unsyncedLogs = activityLogDao.getUnsyncedActivityLogs()
        if (unsyncedLogs.isNotEmpty()) {
            val dtos = unsyncedLogs.map { it.toDto() }
            remote.upload(dtos)
            unsyncedLogs.forEach { log -> activityLogDao.markSynced(log.id, log.completedAt) }
        }

    }
}