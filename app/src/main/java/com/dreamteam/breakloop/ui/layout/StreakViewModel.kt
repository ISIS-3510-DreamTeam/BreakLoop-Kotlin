package com.dreamteam.breakloop.ui.layout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.goal.DataStoreGoalRepository
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.repository.UsageRepositoryImpl
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.repository.GoalRepository
import com.dreamteam.breakloop.domain.repository.UsageRepository
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateBaselineUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateStreakUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class StreakViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application.applicationContext)
    private val usageRepository: UsageRepository =
        UsageRepositoryImpl(
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
    private val calculateStreak = CalculateStreakUseCase()

    private val today = LocalDate.now()

    val streakDays: StateFlow<Int> = combine(
        usageRepository.observeDailyStats(today.minusDays(365).toString(), today.toString()),
        goalRepository.observeDailyGoalMs(),
    ) { days, goalMs ->
        calculateStreak.calculate(days, goalMs, today)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        // make sure the last week exists even if the user never opens Stats
        viewModelScope.launch {
            usageRepository.backfillMissingDays(today.minusDays(7), today.minusDays(1))
        }
    }
}