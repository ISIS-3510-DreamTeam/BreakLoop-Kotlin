package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.dreamteam.breakloop.data.local.entity.FlaggedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlaggedAppDao {
    @Query("SELECT * FROM flagged_apps")
    fun observeFlaggedApps(): Flow<List<FlaggedAppEntity>>

    @Query("SELECT packageName FROM flagged_apps")
    suspend fun getAllPackages(): List<String>

    @Query("INSERT OR REPLACE INTO flagged_apps (packageName, dailyLimitMin, flaggedAt) VALUES (:packageName, :dailyLimitMin, :flaggedAt)")
    suspend fun insertOrReplaceApp(packageName: String, dailyLimitMin: Long?, flaggedAt: Long)

    @Query("DELETE FROM flagged_apps WHERE packageName = :packageName")
    suspend fun deletePackage(packageName: String)

    @Query("UPDATE flagged_apps SET dailyLimitMin = :newLimit WHERE packageName = :packageName")
    suspend fun updateLimit(packageName: String, newLimit: Long?)

    @Query("DELETE FROM flagged_apps WHERE packageName NOT IN (:installedPackages)")
    suspend fun deleteUninstalled(installedPackages: Set<String>)

}