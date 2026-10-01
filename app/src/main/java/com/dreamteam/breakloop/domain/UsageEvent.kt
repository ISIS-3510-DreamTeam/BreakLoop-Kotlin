package com.dreamteam.breakloop.domain

data class UsageEvent (
    val packageName: String,
    val timestamp: Long,
    val type: UsageEventType
)

enum class UsageEventType {
    FOREGROUND,
    BACKGROUND,
    SCREEN_OFF
}