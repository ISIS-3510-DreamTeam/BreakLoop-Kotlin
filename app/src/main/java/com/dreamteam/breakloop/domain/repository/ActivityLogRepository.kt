package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.ActivitySource

interface ActivityLogRepository {
    suspend fun startLog(activityId: String,
                         category: ActivityCategory,
                         source: ActivitySource,
                         context: ContextSnapshot): String
    suspend fun completeLog(logId: String, xp: Int)
    suspend fun getRecentActivityIds(limit: Int): List<String>

    suspend fun syncActivityLogs()
}