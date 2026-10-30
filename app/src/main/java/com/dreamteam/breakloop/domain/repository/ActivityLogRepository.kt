package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.ActivitySource
import kotlinx.coroutines.flow.Flow

interface ActivityLogRepository {
    suspend fun startLog(activityId: String,
                         category: ActivityCategory,
                         source: ActivitySource,
                         context: ContextSnapshot): String
    suspend fun completeLog(logId: String, xp: Int)
    suspend fun getRecentActivityIds(limit: Int): List<String>

    suspend fun syncActivityLogs()

    fun observeOpenLog(): Flow<ActivityLogEntity?>
    suspend fun getOpenLog(activityId: String): ActivityLogEntity?
}