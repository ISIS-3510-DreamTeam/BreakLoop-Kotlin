package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_usage_stats")
data class DailyUsageStatsEntity (
    @PrimaryKey()
    val date: String,
    val screenTimeMs: Long,
    val pickups: Int,
    val unlocks: Int,
    val isPartial: Boolean,
    val updatedAt: Long

)
