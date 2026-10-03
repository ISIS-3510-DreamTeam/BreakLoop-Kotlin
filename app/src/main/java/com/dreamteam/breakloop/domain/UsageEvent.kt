package com.dreamteam.breakloop.domain

import com.dreamteam.breakloop.domain.enums.UsageEventType

data class UsageEvent (
    val packageName: String,
    val timestamp: Long,
    val type: UsageEventType
)
