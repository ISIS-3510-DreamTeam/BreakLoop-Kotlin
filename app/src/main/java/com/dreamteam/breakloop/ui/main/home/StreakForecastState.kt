package com.dreamteam.breakloop.ui.main.home

import com.dreamteam.breakloop.domain.StreakSimulationResult

data class StreakForecastState(
    val isLoading: Boolean = false,
    val result: StreakSimulationResult? = null,
)