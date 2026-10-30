package com.dreamteam.breakloop.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsageDto(
    val date: String,
    val screenTimeMs: Long,
    val pickups: Int,
    val unlocks: Int,
    val isPartial: Boolean,
    val updatedAt: Long,
    val flaggedTimeMs: Long
) {
}