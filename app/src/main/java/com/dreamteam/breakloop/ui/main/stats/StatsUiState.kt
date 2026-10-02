package com.dreamteam.breakloop.ui.main.stats

import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.WeeklySummary

sealed interface StatsUiState {
    data object Loading : StatsUiState
    data object NoPermission : StatsUiState
    data class Content(
        val totalMs: Long,
        val apps: List<AppUsage>,
        val weeklySummary: WeeklySummary
    ) : StatsUiState
}