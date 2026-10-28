package com.dreamteam.breakloop.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "flagged_apps")
data class FlaggedAppEntity (
    @PrimaryKey()
    val packageName: String,
    val dailyLimitMin: Int,
    val flaggedAt: Long

)

