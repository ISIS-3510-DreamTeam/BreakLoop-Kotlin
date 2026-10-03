package com.dreamteam.breakloop.ui.main.home

import com.dreamteam.breakloop.domain.GoalProgress
import com.dreamteam.breakloop.ui.main.stats.StatsUiState

sealed interface HomeUiState {

    data object Loading : HomeUiState
    data object NoPermission : HomeUiState
    data class Content(
        val usedMs: Long,
        val progress: GoalProgress,
        val goalMs: Long?
    ) : HomeUiState
}