package com.dreamteam.breakloop.domain

import com.dreamteam.breakloop.domain.enums.PickupType
import java.util.UUID

data class PickupEvent(
    val id: UUID,
    val timestamp: Long,
    val type: PickupType
)
