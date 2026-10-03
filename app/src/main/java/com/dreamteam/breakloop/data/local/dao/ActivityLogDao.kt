package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity

@Dao
interface ActivityLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(activityLog: ActivityLogEntity)

    @Query("UPDATE activity_log SET completedAt = :completedAt, xp = :xp WHERE id = :activityId")
    suspend fun markActivityAsCompleted(activityId: String, completedAt: Long, xp: Int)

    @Query("SELECT activityId FROM activity_log WHERE completedAt IS NOT NULL ORDER BY completedAt DESC LIMIT :limit")
    suspend fun getRecentCompletedActivityIds(limit: Int): List<String>

}