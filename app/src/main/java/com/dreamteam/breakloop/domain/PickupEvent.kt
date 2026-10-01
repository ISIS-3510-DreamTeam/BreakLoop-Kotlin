package com.dreamteam.breakloop.domain

import java.util.UUID

data class PickupEvent(
    val id: UUID,
    val timestamp: Long,
    val type: PickupType
)

enum class PickupType {
    PICKUP,
    WAKE_ON_TABLE,
    UNLOCK_ONLY
}