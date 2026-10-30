package com.dreamteam.breakloop.ui.main.offline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dreamteam.breakloop.data.repository.ActivityRepositoryImpl
import com.dreamteam.breakloop.data.system.ActivityCatalogDataSource
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.remote.ActivityLogRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.remote.SuggestionInteractionRemoteDataSource
import com.dreamteam.breakloop.data.remote.WeatherApiFactory
import com.dreamteam.breakloop.data.repository.ActivityLogRepositoryImpl
import com.dreamteam.breakloop.data.repository.ContextProviderImpl
import com.dreamteam.breakloop.data.repository.SuggestionInteractionRepositoryImpl
import com.dreamteam.breakloop.data.repository.WeatherRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.dreamteam.breakloop.data.system.InterestsDataSource
import com.dreamteam.breakloop.data.system.LocationDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.PlacePreferenceDataSource
import com.dreamteam.breakloop.domain.recommendation.DefaultScoringRules
import com.dreamteam.breakloop.domain.usecase.RecommendActivityUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class OfflineViewModel(
    application: Application
): AndroidViewModel(application) {

    private val useCase = RecommendActivityUseCase(
        rules = DefaultScoringRules.all
        )

        private val repositoryImpl = ActivityRepositoryImpl(
        catalogDataSource = ActivityCatalogDataSource(application.applicationContext)
    )
    private val _uiState = MutableStateFlow<OfflineUiState>(OfflineUiState.Loading)
    private var availableMin = 15
    private var selectedCategory: ActivityCategory? = null
    private val db = AppDatabase.getInstance(application.applicationContext)

    private var interactionId: String? = null
    private var selectedPlace: PlaceLabel? = null
    private val activityLogRepository = ActivityLogRepositoryImpl(
        db.activityLogDao(),
        remote = ActivityLogRemoteDataSource(RetrofitInstance.activityLogApi, FirebaseAuth.getInstance()),
        syncScheduler = WorkManagerSyncScheduler(application.applicationContext)
    )
    private val permissionsDataSource = PermissionsDataSource(application.applicationContext)

    private val placeDataSource = PlacePreferenceDataSource(
        application.applicationContext
    )
    private val weatherRepository = WeatherRepositoryImpl(
        LocationDataSource(application.applicationContext, permissionsDataSource),
        WeatherApiFactory.create()
    )

    private val suggestionInteractionRepositoryImpl = SuggestionInteractionRepositoryImpl(
        db.suggestionInteractionDao(),
        remote = SuggestionInteractionRemoteDataSource(
            RetrofitInstance.suggestionInteractionApi,
            FirebaseAuth.getInstance()
        ),
        syncScheduler = WorkManagerSyncScheduler(application.applicationContext)
    )
    private val contextProvider = ContextProviderImpl(
        InterestsDataSource(application.applicationContext),
        activityLogRepository,
        weatherRepository
    )

    val uiState : StateFlow<OfflineUiState> = _uiState

    private var refreshJob: Job? = null
    private var lastSnapshot: ContextSnapshot? = null

    init {
        viewModelScope.launch {
            selectedPlace = placeDataSource.getPlace()
            recompute()
        }
    }

    fun onAvailableTimeSelected(min: Int){
        availableMin = min
        recompute(refreshContext = false)
    }

    fun onCategorySelected(category: ActivityCategory?) {
        selectedCategory = category
        recompute(refreshContext = false)
    }

    fun onLocationPermissionResult() {
        recompute(refreshContext = true)
    }

    fun onPlaceSelected(place: PlaceLabel?) {
        selectedPlace = place
        viewModelScope.launch {
            placeDataSource.setPlace(place)
            interactionId?.let {
                suggestionInteractionRepositoryImpl.updatePlace(it, place)
            }
        }
        val current = _uiState.value as? OfflineUiState.Content ?: return
        _uiState.value = current.copy(selectedPlace = place)
    }

    private fun recompute(refreshContext: Boolean = true){
        refreshJob?.cancel()



        refreshJob= viewModelScope.launch {
            val catalog = repositoryImpl.getCatalog()
            val cached = lastSnapshot
            val snapshot = if (refreshContext || cached == null) {
                contextProvider.getSnapshot(availableMin).also { lastSnapshot = it }
            } else {
                cached.copy(availableMin = availableMin)
            }
            val suggestion = useCase.recommend(catalog, snapshot)
            val listSelected = if (selectedCategory != null) {catalog.filter { it.category == selectedCategory} } else catalog
            val id = interactionId ?: suggestionInteractionRepositoryImpl
                .openInteraction(snapshot, System.currentTimeMillis(), selectedPlace)
                .also { interactionId = it }
            _uiState.value = OfflineUiState.Content(
                suggestion = suggestion,
                availableMin = availableMin,
                isWeatherAvailable = snapshot.weather != null,
                activities = listSelected,
                selectedCategory = selectedCategory,
                needsLocationPermission = !permissionsDataSource.hasLocationAccess(),
                selectedPlace = selectedPlace,
                interactionId = interactionId
            )
            if (suggestion != null) {
                suggestionInteractionRepositoryImpl.addShownSuggestion(
                    id = id,
                    activityId = suggestion.activity.id,
                    category = suggestion.activity.category,
                    availableMin = availableMin,
                    score = suggestion.score,
                    reasons = suggestion.reasons
                )
            }
        }

    }
}
