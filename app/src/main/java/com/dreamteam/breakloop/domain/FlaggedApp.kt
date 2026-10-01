package com.dreamteam.breakloop.domain

data class FlaggedApp (
    val packageName: String,
    val label: String,
    val dailyLimitMin: Int
)