package com.dreamteam.breakloop.data.local.entity

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.ZoneId

data class PhonePickupModel(
    val timestamp: Long,
    val pickupId: String = "pickup_${timestamp * 1000}",
    val userId: String = "",
    val isSynced: Boolean = false,
    val hourOfDay: Int = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .hour,
    val dayOfWeek: Int = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .dayOfWeek.value,
    val isIntentional: Boolean = false
) {

    fun toFirestore(): Map<String, Any> {
        return mapOf(
            "pickupId" to pickupId,
            "userId" to userId,
            "timestamp" to Timestamp(
                timestamp / 1000,
                ((timestamp % 1000) * 1_000_000).toInt()
            ),
            "hourOfDay" to hourOfDay,
            "dayOfWeek" to dayOfWeek,
            "isIntentional" to isIntentional
        )
    }
}