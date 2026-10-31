package com.dreamteam.breakloop.domain

data class FlaggedApp (
    val packageName: String,
    val dailyLimitMin: Int?,
    val flaggedAt: Long
)