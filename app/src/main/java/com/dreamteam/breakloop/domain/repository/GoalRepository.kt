package com.dreamteam.breakloop.domain.repository

import kotlinx.coroutines.flow.Flow

interface GoalRepository {

    fun observeDailyGoalMs(): Flow<Long?>

    suspend fun setDailyGoalMs(goalMs: Long)

    suspend fun getLastNotifiedDate(): String?

    suspend fun setLastNotifiedDate(date: String)
}