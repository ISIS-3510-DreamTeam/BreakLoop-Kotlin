package com.dreamteam.breakloop.ui.main.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.local.repository.UsageRepositoryImpl
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsViewModel(
    application: Application
): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application.applicationContext)
    private val repository = UsageRepositoryImpl(
        usageEventsDataSource = UsageEventsDataSource(
            application.applicationContext,
            permissionsDataSource = PermissionsDataSource(application.applicationContext)
        ),
        installedAppsDataSource = InstalledAppsDataSource(application.applicationContext),
        aggregateUsageEventsUseCase = AggregateUsageEventsUseCase(),
        appUsageDao = db.appUsageDao(),
        dailyUsageStatsDao = db.dailyUsageStatsDao()
    )

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    private var refreshJob: Job? = null
    var uiState : StateFlow<StatsUiState> = _uiState
    init {
        refresh()
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val today = LocalDate.now().toString()
            val refresh = repository.refreshDay(today)
            if (!refresh) {
                _uiState.value = StatsUiState.NoPermission
                return@launch
            }

            repository.observeAppUsage(today).collect {
                list -> _uiState.value = StatsUiState.Content(
                    totalMs =  list.sumOf { it.foregroundMs },
                    apps = list.sortedByDescending { it.foregroundMs })
            }
        }

    }

}