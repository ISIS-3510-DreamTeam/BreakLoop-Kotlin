package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(activityLog: ActivityLogEntity)

    @Query("UPDATE activity_log SET completedAt = :completedAt, xp = :xp, synced = 0 WHERE id = :logId")
    suspend fun markActivityAsCompleted(logId: String, completedAt: Long, xp: Int)

    @Query("SELECT activityId FROM activity_log WHERE completedAt IS NOT NULL ORDER BY completedAt DESC LIMIT :limit")
    suspend fun getRecentCompletedActivityIds(limit: Int): List<String>

    @Query("SELECT * FROM activity_log WHERE synced = 0 ORDER BY startedAt ASC")
    suspend fun getUnsyncedActivityLogs(): List<ActivityLogEntity>

    @Transaction
    @Query("UPDATE activity_log SET synced = 1 WHERE id = :logId AND completedAt = :completedAtSubido")
    suspend fun markSynced(logId: String, completedAtSubido: Long?)

    @Query("SELECT * FROM activity_log WHERE activityId = :activityId AND completedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getOpenLog(activityId: String): ActivityLogEntity?

    @Query("SELECT * FROM activity_log WHERE completedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    fun observeOpenLog(): Flow<ActivityLogEntity?>

    @Query("SELECT COALESCE(SUM(xp), 0) FROM activity_log WHERE completedAt >= :startOfDay")
    fun observeXpSince(startOfDay: Long): Flow<Int>

}