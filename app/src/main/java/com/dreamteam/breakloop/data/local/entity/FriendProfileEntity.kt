package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_profiles")
data class FriendProfileEntity (
    @PrimaryKey
    var uid: String,
    var displayName: String,
    var photoUrl: String?,
    var streak: Int,
    val petLevel: String,
    val badgesJson: String,
    val fetchedAt: Long
)

