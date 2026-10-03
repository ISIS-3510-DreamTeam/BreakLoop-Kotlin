package com.dreamteam.breakloop.ui.main.stats.deepstats

sealed interface DeepStatsUiState {

    data object Loading : DeepStatsUiState

    data class Content(
        val dailyPickups: Int = 0,
        val avgIntervalMinutes: Int = 0,
        val baselineText: String = "-",
        val mindfulPercentage: Int = 0,
        val impulsivePercentage: Int = 0
    ) : DeepStatsUiState

    data class Error(
        val message: String
    ) : DeepStatsUiState
}