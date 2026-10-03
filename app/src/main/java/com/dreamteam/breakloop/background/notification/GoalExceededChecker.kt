package com.dreamteam.breakloop.background.notification

import com.dreamteam.breakloop.data.goal.DataStoreGoalRepository
import com.dreamteam.breakloop.domain.GoalProgress
import com.dreamteam.breakloop.domain.repository.GoalRepository
import com.dreamteam.breakloop.domain.usecase.CalculateGoalProgressUseCase
import android.content.Context
import kotlinx.coroutines.flow.first

class GoalExceededChecker(context: Context) {

    private val goalRepository: GoalRepository = DataStoreGoalRepository(context)
    private val calculateGoalProgress = CalculateGoalProgressUseCase()
    private val notifier = GoalNotifier(context.applicationContext)

    suspend fun check(usedMs: Long, today: String) {
        val result = goalRepository.observeDailyGoalMs().first()
        val state = calculateGoalProgress.calculate(usedMs, result)
        if ((state !is GoalProgress.Exceeded) || (goalRepository.getLastNotifiedDate() == today)) {return}
        notifier.notifyGoalExceeded(state.exceededMs)
        goalRepository.setLastNotifiedDate(today)

    }
}