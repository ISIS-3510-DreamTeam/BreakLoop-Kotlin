package com.dreamteam.breakloop.domain

import android.icu.text.CaseMap
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import com.dreamteam.breakloop.domain.enums.WeatherCondition
import kotlin.time.Duration

data class OfflineActivity (
    val id: String,
    val title: String,
    val category: ActivityCategory,
    val durationMin: Int,
    val xp: Int,
    val prompt: String,
    val isOutdoor: Boolean,
    val suitableWeather: Set<WeatherCondition>,
    val suitableTimesOfDay: Set<TimeOfDay>,
)