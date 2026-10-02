package com.dreamteam.breakloop.ui.main.offline.components

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dreamteam.breakloop.data.repository.ActivityRepositoryImpl
import com.dreamteam.breakloop.data.system.ActivityCatalogDataSource
import com.dreamteam.breakloop.domain.enums.ActivityPhase
import com.dreamteam.breakloop.ui.navigation.OfflineActivityDetail
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val activityId = savedStateHandle.toRoute<OfflineActivityDetail>().activityId

    private val activityRepository =
        ActivityRepositoryImpl(ActivityCatalogDataSource(application.applicationContext))
    private val _uiState = MutableStateFlow<ActivityDetailUiState>(ActivityDetailUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val activity = activityRepository.getActivity(activityId)
            _uiState.value = if (activity == null) {
                ActivityDetailUiState.NotFound
            } else {
                ActivityDetailUiState.Content(
                    activity,
                    ActivityPhase.READY,
                    activity.durationMin * 60
                )
            }
        }
    }

    fun start() {
        timerJob?.cancel() // por si tocan Start dos veces
        val current = _uiState.value as? ActivityDetailUiState.Content ?: return
        _uiState.value = current.copy(phase = ActivityPhase.RUNNING)

        timerJob = viewModelScope.launch {
            while ((currentContent()?.remainingSeconds ?: 0) > 0) {
                delay(1000)
                val current = currentContent() ?: return@launch
                _uiState.value = current.copy(remainingSeconds = current.remainingSeconds - 1)
            }
            val current = currentContent() ?: return@launch
            _uiState.value = current.copy(phase = ActivityPhase.COMPLETED)
            // TODO OFF-09: guardar el registro con su XP
        }
    }

    fun finish() {
        timerJob?.cancel()
        val current = currentContent() ?: return
        _uiState.value = current.copy(phase = ActivityPhase.COMPLETED)
        // TODO OFF-09: guardar el registro
    }

    private fun currentContent(): ActivityDetailUiState.Content? =
        _uiState.value as? ActivityDetailUiState.Content
}