package com.dreamteam.breakloop.ui.main.focus

data class FocusSessionUiState
    (
        val selectedDuration: Int = 25,
        val customDuration: Int = 25,
        val focusGoal: String = "",
        val selectedSoundscape: Soundscape = Soundscape.HEARTH,
        val appShieldEnabled: Boolean = true,
        val isLoading: Boolean = false,
        val error: String? = null
            )
enum class Soundscape {
    HEARTH,
    RAIN,
    SILENCE
}