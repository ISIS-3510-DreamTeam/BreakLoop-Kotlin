package com.dreamteam.breakloop.ui.main.achievements

import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.domain.CatalogSource

sealed interface AchievementsUiState {
    data object Loading : AchievementsUiState
    data object Error : AchievementsUiState
    data class Content(
        val items: List<AchievementProgress>,
        val source: CatalogSource,
    ) : AchievementsUiState {
        val unlockedCount: Int get() = items.count { it.unlocked }
    }
}