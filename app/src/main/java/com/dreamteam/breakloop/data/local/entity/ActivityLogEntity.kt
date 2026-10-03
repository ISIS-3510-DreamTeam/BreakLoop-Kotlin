package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dreamteam.breakloop.domain.enums.ActivitySource
import com.dreamteam.breakloop.domain.enums.TimeOfDay

@Entity(tableName = "activity_log")
data class ActivityLogEntity (
    @PrimaryKey
    var id: String,
    var activityId: String,
    var source: ActivitySource,
    var startedAt: Long,
    var completedAt: Long?,
    var xp: Int,
    var timeOfDay: TimeOfDay,
    val weather: String?,
    val availableMin: Int,
)