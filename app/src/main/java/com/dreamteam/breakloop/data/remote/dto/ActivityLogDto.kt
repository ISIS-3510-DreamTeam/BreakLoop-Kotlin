package com.dreamteam.breakloop.data.remote.dto

import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.ActivitySource
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import kotlinx.serialization.Serializable

@Serializable
data class ActivityLogDto(
    val id: String,
    val activityId: String,
    val category: ActivityCategory,
    val source: ActivitySource,
    val interactionId: String?,
    val startedAt: Long,
    val completedAt: Long?,
    val xp: Int,
    val timeOfDay: TimeOfDay,
    val weather: String?,
    val availableMin: Int,
    val placeLabel: PlaceLabel?,
    val latitude: Double?,
    val longitude: Double?
)
