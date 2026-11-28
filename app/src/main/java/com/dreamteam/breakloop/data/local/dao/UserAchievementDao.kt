package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dreamteam.breakloop.data.local.entity.UserAchievementEntity

@Dao
interface UserAchievementDao {

    /** IGNORE: an achievement that is already stored keeps its first unlock date. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(unlocks: List<UserAchievementEntity>)

    @Query("SELECT * FROM user_achievements WHERE uid = :uid")
    suspend fun getForUser(uid: String): List<UserAchievementEntity>

    @Query("SELECT * FROM user_achievements WHERE synced = 0")
    suspend fun getUnsynced(): List<UserAchievementEntity>

    @Query("UPDATE user_achievements SET synced = 1 WHERE uid = :uid AND achievementId = :achievementId")
    suspend fun markSynced(uid: String, achievementId: String)
}