package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.ActivitySource
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import com.dreamteam.breakloop.domain.enums.TimeOfDay

@Entity(tableName = "activity_log")
data class ActivityLogEntity (
    @PrimaryKey
    var id: String,
    var activityId: String,
    var category: ActivityCategory,
    var source: ActivitySource,
    var interactionId: String? = null,
    var startedAt: Long,
    var completedAt: Long? = null,
    var xp: Int,
    var timeOfDay: TimeOfDay,
    val weather: String? = null,
    val availableMin: Int,
    val placeLabel: PlaceLabel? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val synced: Boolean = false
)