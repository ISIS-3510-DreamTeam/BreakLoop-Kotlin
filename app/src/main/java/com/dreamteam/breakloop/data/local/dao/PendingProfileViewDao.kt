package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dreamteam.breakloop.data.local.entity.PendingProfileViewEntity

@Dao
interface PendingProfileViewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert( pendingLog: PendingProfileViewEntity)

    @Query("SELECT * FROM pending_profile_view_log")
    suspend fun getAll(): List<PendingProfileViewEntity>

    @Query("DELETE FROM pending_profile_view_log WHERE viewedAt = :viewedAt")
    suspend fun delete(viewedAt: Long)

}