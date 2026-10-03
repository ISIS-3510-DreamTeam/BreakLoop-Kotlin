package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.GoalProgress

class CalculateGoalProgressUseCase {
    fun calculate(usedMs: Long, goalMs: Long?): GoalProgress {
        return when {
            (goalMs == null || goalMs <= 0) -> GoalProgress.NotSet
            (goalMs < usedMs) -> GoalProgress.Exceeded(usedMs,goalMs)
            ((goalMs*0.8) <= usedMs) -> GoalProgress.NearLimit(usedMs,goalMs)
            else -> GoalProgress.OnTrack(usedMs,goalMs)
        }
    }
}