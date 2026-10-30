package com.dreamteam.breakloop.ui.main.offline.components.activeactivity

import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ActivityPhase

sealed interface ActiveActivityUiState {
    data object Hidden: ActiveActivityUiState
    data class Running(
        val activityId: String,
        val title: String,
        val remainingSeconds:Int
    ): ActiveActivityUiState

    data class Finished(
        val activityId: String,
        val title: String
    ): ActiveActivityUiState
}