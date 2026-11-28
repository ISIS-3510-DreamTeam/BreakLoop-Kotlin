package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dreamteam.breakloop.data.local.entity.FriendProfileEntity

@Dao
interface FriendProfileDao {

    @Query("SELECT * FROM friend_profiles WHERE UID =:friendUid")
    suspend fun getFriendProfileByUid(friendUid: String): FriendProfileEntity?

    @Upsert
    suspend fun upsert(profile: FriendProfileEntity)
}