package com.dreamteam.breakloop.ui.main.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.local.repository.UsageRepositoryImpl
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import com.dreamteam.breakloop.domain.usecase.BuildWeeklySummaryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

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

    private val buildWeeklySummaryUseCase = BuildWeeklySummaryUseCase()

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    private var refreshJob: Job? = null
    var uiState : StateFlow<StatsUiState> = _uiState
    init {
        refresh()
    }

    fun refresh() {

        val today = LocalDate.now()
        val todayStr = today.toString()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)
        val lastMonday = monday.minusWeeks(1)
        val lastSunday = lastMonday.plusDays(6)


        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val refresh = repository.refreshDay(todayStr)
            if (!refresh) {
                _uiState.value = StatsUiState.NoPermission
                return@launch
            }

            combine(
                repository.observeAppUsage(todayStr),
                repository.observeDailyStats(monday.toString(), sunday.toString()),
                repository.observeDailyStats(lastMonday.toString(), lastSunday.toString())
            ) { apps, thisWeek, lastWeek ->
                StatsUiState.Content(
                    totalMs = apps.sumOf { it.foregroundMs },
                    apps = apps.sortedByDescending { it.foregroundMs },
                    weeklySummary = buildWeeklySummaryUseCase.buildSummary(
                        thisWeek, lastWeek, monday, todayStr
                    )
                )
            }.collect { state ->
                _uiState.value = state
            }
        }

    }

}