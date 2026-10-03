package com.dreamteam.breakloop.ui.main.offline

import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.Recommendation
import com.dreamteam.breakloop.domain.enums.ActivityCategory

sealed interface OfflineUiState {
    data object Loading: OfflineUiState
    data class Content(
        val suggestion: Recommendation?,
        val availableMin: Int,
        val isWeatherAvailable: Boolean,
        val activities: List<OfflineActivity>,
        val selectedCategory: ActivityCategory?,
    ): OfflineUiState
}