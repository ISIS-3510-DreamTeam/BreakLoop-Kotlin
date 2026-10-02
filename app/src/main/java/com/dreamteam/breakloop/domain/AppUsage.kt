package com.dreamteam.breakloop.domain

import java.util.Date

data class AppUsage (
    val date: String,
    val packageName: String,
    val foregroundMs: Long
)