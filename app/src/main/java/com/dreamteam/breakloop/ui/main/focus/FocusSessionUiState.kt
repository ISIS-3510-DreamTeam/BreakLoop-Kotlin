package com.dreamteam.breakloop.ui.main.focus

data class FocusSessionUiState(
    val selectedDuration: Int = 25,
    val customDuration: Int = 25,
    val isCustomDurationSelected: Boolean = false,

    val focusGoal: String = "",
    val selectedSoundscape: Soundscape = Soundscape.SILENCE,
    val appShieldEnabled: Boolean = false,

    val isSessionActive: Boolean = false,
    val isPaused: Boolean = false,

    val remainingSeconds: Int = 0,
    val elapsedSeconds: Int = 0,

    val isLoading: Boolean = false,
    val error: String? = null
)