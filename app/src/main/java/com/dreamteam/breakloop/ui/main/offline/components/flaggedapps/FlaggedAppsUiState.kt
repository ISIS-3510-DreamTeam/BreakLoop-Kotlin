package com.dreamteam.breakloop.ui.main.offline.components.flaggedapps


sealed interface FlaggedAppsUiState {
    data object Loading : FlaggedAppsUiState

    data object Empty : FlaggedAppsUiState

    data class Content(
        val flaggedApps: List<FlaggedAppRow>,
        val editing : FlaggedAppRow?
    ) : FlaggedAppsUiState
}