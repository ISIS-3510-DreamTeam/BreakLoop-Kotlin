package com.dreamteam.breakloop.ui.main.offline.components

import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ActivityPhase

sealed interface ActivityDetailUiState {
    data object Loading: ActivityDetailUiState
    data object NotFound: ActivityDetailUiState
    data class Content(
        val activity: OfflineActivity,
        val phase: ActivityPhase,
        val remainingSeconds: Int
    ): ActivityDetailUiState
}