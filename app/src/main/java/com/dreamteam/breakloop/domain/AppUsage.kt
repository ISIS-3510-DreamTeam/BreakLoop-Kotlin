package com.dreamteam.breakloop.domain

import java.util.Date

data class AppUsage (
    val date: Date,
    val packageName: String,
    val foregroundMs: Long
)