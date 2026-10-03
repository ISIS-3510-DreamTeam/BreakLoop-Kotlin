package com.dreamteam.breakloop.ui.main.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dreamteam.breakloop.data.local.entity.FocusSessionEntity
import com.dreamteam.breakloop.data.local.repository.FocusSessionRepository
import com.dreamteam.breakloop.data.local.worker.FocusSessionWorker
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusViewModel(
    private val focusSessionRepository: FocusSessionRepository,
    private val appContext: Context,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FocusSessionUiState()
    )

    val uiState: StateFlow<FocusSessionUiState> =
        _uiState.asStateFlow()

    private var timerJob: Job? = null

    private var sessionId: String? = null

    private var sessionStartTime: Long? = null

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

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            _uiState.update {
                it.copy(
                    error = "You must be logged in to start a focus session"
                )
            }
            return
        }

        timerJob?.cancel()

        sessionId = UUID.randomUUID().toString()
        sessionStartTime = System.currentTimeMillis()

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
                        remainingSeconds = (
                                it.remainingSeconds - 1
                                ).coerceAtLeast(0),

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

        if (!_uiState.value.isSessionActive) {
            return
        }

        saveSession(
            status = "ABANDONED"
        )

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

        sessionId = null
        sessionStartTime = null
    }

    private fun completeSession() {

        println("FOCUS DEBUG: Timer completed")

        timerJob?.cancel()
        timerJob = null

        saveSession(
            status = "COMPLETED"
        )
    }

    private fun saveSession(
        status: String
    ) {

        val currentSessionId = sessionId
        val currentStartTime = sessionStartTime
        val currentState = _uiState.value

        val uid = firebaseAuth.currentUser?.uid

        println(
            "FOCUS DEBUG: id=$currentSessionId " +
                    "uid=$uid " +
                    "duration=${currentState.selectedDuration}"
        )

        if (
            currentSessionId == null ||
            currentStartTime == null ||
            uid == null
        ) {
            _uiState.update {
                it.copy(
                    isSessionActive = false,
                    isPaused = false,
                    remainingSeconds = 0,
                    error = "Could not save focus session"
                )
            }

            return
        }

        val session = FocusSessionEntity(
            id = currentSessionId,
            uid = uid,
            startTime = currentStartTime,
            duration = currentState.selectedDuration,
            type = "FOCUS",
            status = status,
            xpEarned = 0,
            synced = false
        )

        viewModelScope.launch {

            try {

                focusSessionRepository.saveSession(
                    session
                )

                scheduleSync()

                _uiState.update {
                    it.copy(
                        isSessionActive = false,
                        isPaused = false,
                        remainingSeconds = 0,
                        error = null
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isSessionActive = false,
                        isPaused = false,
                        remainingSeconds = 0,
                        error = "Could not save focus session"
                    )
                }
            }
        }
    }

    private fun scheduleSync() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                NetworkType.CONNECTED
            )
            .build()

        val workRequest =
            OneTimeWorkRequestBuilder<FocusSessionWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(appContext)
            .enqueueUniqueWork(
                "focus-session-sync",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }
}