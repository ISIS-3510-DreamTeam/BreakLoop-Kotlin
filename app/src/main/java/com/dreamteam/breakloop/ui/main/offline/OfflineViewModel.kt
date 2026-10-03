package com.dreamteam.breakloop.ui.main.offline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dreamteam.breakloop.data.repository.ActivityRepositoryImpl
import com.dreamteam.breakloop.data.system.ActivityCatalogDataSource
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.repository.ActivityLogRepositoryImpl
import com.dreamteam.breakloop.data.repository.ContextProviderImpl
import com.dreamteam.breakloop.data.system.InterestsDataSource
import com.dreamteam.breakloop.domain.recommendation.DefaultScoringRules
import com.dreamteam.breakloop.domain.usecase.RecommendActivityUseCase
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
    private val activityLogRepository = ActivityLogRepositoryImpl(db.activityLogDao())
    private val contextProvider = ContextProviderImpl(
        InterestsDataSource(application.applicationContext),
        activityLogRepository
    )

    val uiState : StateFlow<OfflineUiState> = _uiState

    private var refreshJob: Job? = null

    init {
        recompute()
    }

    fun onAvailableTimeSelected(min: Int){
        availableMin = min
        recompute()
    }

    fun onCategorySelected(category: ActivityCategory?) {
        selectedCategory = category
        recompute()
    }

    private fun recompute(){
        refreshJob?.cancel()

        refreshJob= viewModelScope.launch {
            val catalog = repositoryImpl.getCatalog()
            val snapshot = contextProvider.getSnapshot(availableMin)
            val suggestion = useCase.recommend(catalog, snapshot)
            val listSelected = if (selectedCategory != null) {catalog.filter { it.category == selectedCategory} } else catalog
            _uiState.value = OfflineUiState.Content(
                suggestion = suggestion,
                availableMin= availableMin ,
                isWeatherAvailable = snapshot.weather != null,
                activities = listSelected,
                selectedCategory = selectedCategory
            )
            }
    }
}