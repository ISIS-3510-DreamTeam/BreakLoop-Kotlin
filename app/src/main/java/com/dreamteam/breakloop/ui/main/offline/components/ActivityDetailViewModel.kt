package com.dreamteam.breakloop.ui.main.offline.components

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.repository.ActivityLogRepositoryImpl
import com.dreamteam.breakloop.data.repository.ActivityRepositoryImpl
import com.dreamteam.breakloop.data.repository.ContextProviderImpl
import com.dreamteam.breakloop.data.system.ActivityCatalogDataSource
import com.dreamteam.breakloop.data.system.InterestsDataSource
import com.dreamteam.breakloop.domain.enums.ActivityPhase
import com.dreamteam.breakloop.domain.enums.ActivitySource
import com.dreamteam.breakloop.domain.repository.ActivityLogRepository
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
    private val route = savedStateHandle.toRoute<OfflineActivityDetail>()
    private val activityId = route.activityId

    private val source = if(route.fromSuggestion) ActivitySource.RECOMMENDED else ActivitySource.BROWSED

    private val activityRepository =
        ActivityRepositoryImpl(ActivityCatalogDataSource(application.applicationContext))

    private val db = AppDatabase.getInstance(application.applicationContext)
    private val activityLogRepository = ActivityLogRepositoryImpl(db.activityLogDao())
    private val contextProvider =
        ContextProviderImpl(InterestsDataSource(application.applicationContext),
            activityLogRepository)

    private var logId: String? = null
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
    private fun saveComplexion(xp: Int){
        val id =  logId?: return
        viewModelScope.launch {
            activityLogRepository.completeLog(id, xp)
        }
    }
    fun start() {
        timerJob?.cancel() // por si tocan Start dos veces
        val current = _uiState.value as? ActivityDetailUiState.Content ?: return
        _uiState.value = current.copy(phase = ActivityPhase.RUNNING)

        viewModelScope.launch {
            val snapshot = contextProvider.getSnapshot(current.activity.durationMin)
            logId = activityLogRepository.startLog(current.activity.id, source, snapshot)
        }
        timerJob = viewModelScope.launch {
            while ((currentContent()?.remainingSeconds ?: 0) > 0) {
                delay(1000)
                val current = currentContent() ?: return@launch
                _uiState.value = current.copy(remainingSeconds = current.remainingSeconds - 1)
            }
            val current = currentContent() ?: return@launch
            _uiState.value = current.copy(phase = ActivityPhase.COMPLETED)
            saveComplexion(current.activity.xp)
        }
    }

    fun finish() {
        timerJob?.cancel()
        val current = currentContent() ?: return
        _uiState.value = current.copy(phase = ActivityPhase.COMPLETED)
        saveComplexion(current.activity.xp)
    }

    private fun currentContent(): ActivityDetailUiState.Content? =
        _uiState.value as? ActivityDetailUiState.Content
}