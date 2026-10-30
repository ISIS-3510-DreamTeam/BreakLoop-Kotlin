package com.dreamteam.breakloop.ui.main.offline.components

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.remote.ActivityLogRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.remote.WeatherApiFactory
import com.dreamteam.breakloop.data.repository.ActivityLogRepositoryImpl
import com.dreamteam.breakloop.data.repository.ActivityRepositoryImpl
import com.dreamteam.breakloop.data.repository.ContextProviderImpl
import com.dreamteam.breakloop.data.repository.WeatherRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.dreamteam.breakloop.data.system.ActivityCatalogDataSource
import com.dreamteam.breakloop.data.system.InterestsDataSource
import com.dreamteam.breakloop.data.system.LocationDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.PlacePreferenceDataSource
import com.dreamteam.breakloop.data.remote.SuggestionInteractionRemoteDataSource
import com.dreamteam.breakloop.data.repository.SuggestionInteractionRepositoryImpl
import com.dreamteam.breakloop.domain.enums.InteractionOutcome
import com.google.firebase.auth.FirebaseAuth
import com.dreamteam.breakloop.domain.enums.ActivityPhase
import com.dreamteam.breakloop.domain.enums.ActivitySource
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
    private val activityLogRepository = ActivityLogRepositoryImpl(
        db.activityLogDao(),
        remote = ActivityLogRemoteDataSource(RetrofitInstance.activityLogApi, FirebaseAuth.getInstance()),
        syncScheduler = WorkManagerSyncScheduler(application.applicationContext)
    )
    private val placeDataSource = PlacePreferenceDataSource(application.applicationContext)
    private val suggestionInteractionRepository = SuggestionInteractionRepositoryImpl(
        db.suggestionInteractionDao(),
        remote = SuggestionInteractionRemoteDataSource(
            RetrofitInstance.suggestionInteractionApi,
            FirebaseAuth.getInstance()
        ),
        syncScheduler = WorkManagerSyncScheduler(application.applicationContext)
    )
    private val weatherRepository = WeatherRepositoryImpl(
        LocationDataSource(application.applicationContext, PermissionsDataSource(application.applicationContext)),
        WeatherApiFactory.create()
    )
    private val contextProvider =
        ContextProviderImpl(InterestsDataSource(application.applicationContext),
            activityLogRepository,
            weatherRepository)

    private var logId: String? = null
    private val _uiState = MutableStateFlow<ActivityDetailUiState>(ActivityDetailUiState.Loading)

    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    private var endAt: Long? = null
    private var startJob: Job? = null

    init {
        viewModelScope.launch {
            val activity = activityRepository.getActivity(activityId)
            if ( activity == null){
                _uiState.value =
                    ActivityDetailUiState.NotFound
                return@launch
            }
            val open = activityLogRepository.getOpenLog(activityId)
            if ( open == null){
                _uiState.value =
                    ActivityDetailUiState.Content(
                        activity,
                        ActivityPhase.READY,
                        activity.durationMin * 60
                    )
                viewModelScope.launch {
                    activityLogRepository.observeOpenLog().collect { log ->
                        val current = currentContent() ?: return@collect
                        if (current.phase != ActivityPhase.READY) {
                            return@collect
                        }
                        if (log!= null && log.activityId != activityId) {
                            _uiState.value = current.copy(blockedBy = BlockingActivity(log.activityId, activityRepository.getActivity(log.activityId)?.title ?: ""))
                        } else {
                            _uiState.value = current.copy(blockedBy = null)
                        }

                    }
                }
                return@launch
            }
            logId = open.id
            endAt = open.startedAt + activity.durationMin * 60000
            if(System.currentTimeMillis() < endAt!!) {
                _uiState.value = ActivityDetailUiState.Content(
                    activity,
                    ActivityPhase.RUNNING,
                    ((endAt!! - System.currentTimeMillis()) / 1000).toInt()
                )
                startTicker()
            } else {
                _uiState.value = ActivityDetailUiState.Content(
                    activity,
                    ActivityPhase.RUNNING,
                    0
                )
                finish()
            }
        }
    }

    private fun startTicker(){
        timerJob?.cancel() // por si tocan Start dos veces
        val current = _uiState.value as? ActivityDetailUiState.Content ?: return
        _uiState.value = current.copy(phase = ActivityPhase.RUNNING)

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = currentContent() ?: return@launch
                val remainingMs = (endAt ?: (0L)) - System.currentTimeMillis()
                if (remainingMs <= 0) {
                    break
                }
                val seconds = (remainingMs + 999)/1000
                _uiState.value = current.copy(remainingSeconds = seconds.toInt())
            }
            finish()
        }
    }
    fun start() {
        val current = currentContent() ?: return
        if (current.phase != ActivityPhase.READY) {
            return
        }
        val startedAt = System.currentTimeMillis()
        endAt = startedAt + current.activity.durationMin * 60000
        _uiState.value = current.copy(phase = ActivityPhase.RUNNING, remainingSeconds = ((endAt!! - System.currentTimeMillis()) / 1000).toInt())
        startJob = viewModelScope.launch {
            val snapshot = contextProvider.getSnapshot(current.activity.durationMin)
            logId = activityLogRepository.startLog(
                current.activity.id,
                current.activity.category,
                source,
                snapshot,
                interactionId = route.interactionId,
                placeLabel = placeDataSource.getPlace()
            )
            route.interactionId?.let { interactionId ->
                val accepted = route.fromSuggestion
                suggestionInteractionRepository.closeInteraction(
                    id = interactionId,
                    outcome = if (accepted) InteractionOutcome.ACCEPTED else InteractionOutcome.OTHER_ACTIVITY,
                    acceptedActivityId = if (accepted) current.activity.id else null,
                    chosenActivityId = current.activity.id
                )
            }
        }

        startTicker()
    }

    fun finish() {
        timerJob?.cancel()
        val current = currentContent() ?: return
        _uiState.value = current.copy(phase = ActivityPhase.COMPLETED)
        viewModelScope.launch {
            startJob?.join()
            val id = logId ?: return@launch
            activityLogRepository.completeLog(id, current.activity.xp)
        }
    }

    private fun currentContent(): ActivityDetailUiState.Content? =
        _uiState.value as? ActivityDetailUiState.Content
}