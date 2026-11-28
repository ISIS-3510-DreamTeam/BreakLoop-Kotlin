package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_profile_view_log")
data class PendingProfileViewEntity(
    @PrimaryKey val viewedAt: Long
)
