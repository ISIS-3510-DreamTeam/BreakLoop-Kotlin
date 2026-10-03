package com.dreamteam.breakloop.domain

import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import com.dreamteam.breakloop.domain.enums.WeatherCondition

data class ContextSnapshot (
    val timeOfDay: TimeOfDay,
    val isWeekend: Boolean,
    val availableMin: Int,
    val weather: WeatherCondition?,
    val interests: Set<ActivityCategory>,
    val recentActivityIds: List<String>, // MÁS RECIENTE PRIMEROOO
)