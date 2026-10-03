package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.local.dao.ActivityLogDao
import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.ActivitySource
import com.dreamteam.breakloop.domain.repository.ActivityLogRepository
import java.util.UUID

class ActivityLogRepositoryImpl (
    private val activityLogDao: ActivityLogDao
): ActivityLogRepository{
    override suspend fun startLog(
        activityId: String,
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
                availableMin = context.availableMin
        ))
        return id
    }

    override suspend fun completeLog(logId: String, xp: Int) {
        activityLogDao.markActivityAsCompleted(logId, System.currentTimeMillis(), xp)
    }

    override suspend fun getRecentActivityIds(limit: Int): List<String> {
        return activityLogDao.getRecentCompletedActivityIds(limit)
    }
}