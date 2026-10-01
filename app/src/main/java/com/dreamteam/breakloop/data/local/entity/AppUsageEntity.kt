package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity

@Entity(tableName = "app_usage", primaryKeys = ["date", "packageName"])
data class AppUsageEntity(
    val date: String,
    val packageName: String,
    val foregroundMs: Long
)
