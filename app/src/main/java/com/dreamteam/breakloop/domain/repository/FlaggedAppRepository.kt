package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.data.local.entity.FlaggedAppEntity
import com.dreamteam.breakloop.domain.FlaggedApp
import kotlinx.coroutines.flow.Flow

interface FlaggedAppRepository {
    suspend fun getAllApps(): List<String>

    fun getFlaggedApps(): Flow<List<FlaggedApp>>

    suspend fun addFlaggedApp(packageName: String, limit: Long?)

    suspend fun deleteUninstalledApps(packages: Set<String>)

    suspend fun changeFlaggedAppLimit(packageName: String, limit: Long)
}