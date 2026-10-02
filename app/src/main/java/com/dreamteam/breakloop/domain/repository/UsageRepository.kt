package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.DailyUsageStats
import kotlinx.coroutines.flow.Flow

interface UsageRepository {
    suspend fun refreshDay( date: String): Boolean
    fun observeAppUsage(date: String): Flow<List<AppUsage>>
    fun observeDailyStats(startDate: String, endDate: String): Flow<List<DailyUsageStats>>
}