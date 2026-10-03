package com.dreamteam.breakloop.ui.main.home

import android.app.Application
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.goal.DataStoreGoalRepository
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.repository.UsageRepositoryImpl
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.DailyUsageStats
import com.dreamteam.breakloop.domain.repository.GoalRepository
import com.dreamteam.breakloop.domain.repository.UsageRepository
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateBaselineUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateGoalProgressUseCase
import com.dreamteam.breakloop.ui.account.auth.AuthUiState
import com.dreamteam.breakloop.ui.main.stats.StatsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application.applicationContext)
    private val usageRepository: UsageRepository = UsageRepositoryImpl(
        usageEventsDataSource = UsageEventsDataSource(
            application.applicationContext,
            permissionsDataSource = PermissionsDataSource(application.applicationContext)
        ),
        installedAppsDataSource = InstalledAppsDataSource(application.applicationContext),
        aggregateUsageEventsUseCase = AggregateUsageEventsUseCase(),
        appUsageDao = db.appUsageDao(),
        dailyUsageStatsDao = db.dailyUsageStatsDao(),
        calculateBaselineUseCase = CalculateBaselineUseCase()
    )
    private val goalRepository: GoalRepository = DataStoreGoalRepository(application)
    private val calculateGoalProgress = CalculateGoalProgressUseCase()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading) //????
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null
    init {
        refresh()
    }

    fun refresh() {

        val today = LocalDate.now()
        val todayStr = today.toString()

        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val refresh = usageRepository.refreshDay(todayStr)
            if (!refresh) {
                _uiState.value = HomeUiState.NoPermission
                return@launch
            }
            combine( // combina los flows
                usageRepository.observeDailyStats(todayStr, todayStr),
                goalRepository.observeDailyGoalMs()
            ) { stats, goalMs ->
                val usedMs = stats.firstOrNull()?.screenTimeMs ?: 0L // 0 dedault if not
                val goal = calculateGoalProgress.calculate(usedMs,goalMs)
                HomeUiState.Content(
                    usedMs = usedMs,
                    progress = goal,
                    goalMs = goalMs
                )
                // stats is a List with 0 or 1 items → get usedMs (0L if empty)
                // build HomeUiState.Content using calculateGoalProgress
            }.collect { state -> _uiState.value = state }
        }


    }

    fun saveGoal(hours: Int, minutes: Int) {
        viewModelScope.launch {
            goalRepository.setDailyGoalMs(hours * 60 * 60 * 1000L + minutes * 60 * 1000L)
        }
    }

}