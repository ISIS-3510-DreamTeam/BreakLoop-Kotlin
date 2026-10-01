package com.dreamteam.breakloop.domain

import java.util.Date

data class DailyUsageStats (
    val date: Date,
    val screenTimeMs: Long,
    val pickups: Int,
    val unlocks: Int,
    val isPartial: Boolean,
    val updatedAt: Date
)