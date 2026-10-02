package com.dreamteam.breakloop.ui.main.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusSessionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FocusSessionUiState())

    val uiState: StateFlow<FocusSessionUiState> =
        _uiState.asStateFlow()

    fun onDurationChange(duration: Int) {
        _uiState.update {
            it.copy(
                selectedDuration = duration,
                error = null
            )
        }
    }

    fun onCustomDurationChange(duration: Int) {
        _uiState.update {
            it.copy(
                customDuration = duration,
                selectedDuration = duration,
                error = null
            )
        }
    }

    fun onFocusGoalChange(goal: String) {
        _uiState.update {
            it.copy(
                focusGoal = goal,
                error = null
            )
        }
    }

    fun onSoundscapeChange(soundscape: Soundscape) {
        _uiState.update {
            it.copy(selectedSoundscape = soundscape)
        }
    }

    fun onAppShieldChange(enabled: Boolean) {
        _uiState.update {
            it.copy(appShieldEnabled = enabled)
        }
    }

    fun startFocus() {
        if (uiState.value.selectedDuration <= 0) {
            _uiState.update {
                it.copy(error = "Select a valid session duration")
            }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }



}