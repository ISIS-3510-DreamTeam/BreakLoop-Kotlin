package com.dreamteam.breakloop.domain

data class DayBar (
    val dayOfWeek : java.time.DayOfWeek,
    val screenTimeMs: Long?
)