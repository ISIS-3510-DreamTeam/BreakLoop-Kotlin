package com.dreamteam.breakloop.ui.main.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FocusSessionUiState())
    val uiState: StateFlow<FocusSessionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onDurationChange(duration: Int) {
        _uiState.update {
            it.copy(
                selectedDuration = duration,
                isCustomDurationSelected = false,
                error = null
            )
        }
    }

    fun onCustomDurationSelected() {
        _uiState.update {
            it.copy(
                selectedDuration = it.customDuration,
                isCustomDurationSelected = true,
                error = null
            )
        }
    }

    fun onCustomDurationChange(duration: Int) {
        _uiState.update {
            it.copy(
                customDuration = duration,
                selectedDuration = duration,
                isCustomDurationSelected = true,
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
            it.copy(
                selectedSoundscape = soundscape
            )
        }
    }

    fun onAppShieldChange(enabled: Boolean) {
        _uiState.update {
            it.copy(
                appShieldEnabled = enabled
            )
        }
    }

    fun startFocus() {
        val duration = _uiState.value.selectedDuration

        if (duration <= 0) {
            _uiState.update {
                it.copy(
                    error = "Select a valid session duration"
                )
            }
            return
        }

        timerJob?.cancel()

        val totalSeconds = duration * 60

        _uiState.update {
            it.copy(
                isSessionActive = true,
                isPaused = false,
                remainingSeconds = totalSeconds,
                elapsedSeconds = 0,
                isLoading = false,
                error = null
            )
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {

            while (_uiState.value.remainingSeconds > 0) {

                delay(1000)

                if (_uiState.value.isPaused) {
                    continue
                }

                _uiState.update {
                    it.copy(
                        remainingSeconds =
                            (it.remainingSeconds - 1).coerceAtLeast(0),
                        elapsedSeconds =
                            it.elapsedSeconds + 1
                    )
                }
            }

            if (
                _uiState.value.remainingSeconds == 0 &&
                _uiState.value.isSessionActive
            ) {
                completeSession()
            }
        }
    }

    fun pauseFocus() {
        if (!_uiState.value.isSessionActive) {
            return
        }

        _uiState.update {
            it.copy(
                isPaused = true
            )
        }
    }

    fun resumeFocus() {
        if (!_uiState.value.isSessionActive) {
            return
        }

        _uiState.update {
            it.copy(
                isPaused = false
            )
        }
    }

    fun stopFocus() {
        timerJob?.cancel()
        timerJob = null

        _uiState.update {
            it.copy(
                isSessionActive = false,
                isPaused = false,
                remainingSeconds = 0,
                elapsedSeconds = 0
            )
        }
    }

    private fun completeSession() {
        timerJob?.cancel()
        timerJob = null

        _uiState.update {
            it.copy(
                isSessionActive = false,
                isPaused = false,
                remainingSeconds = 0
            )
        }

        // Later: Save the completed session through repository.
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null

        super.onCleared()
    }
}