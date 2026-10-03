package com.dreamteam.breakloop.domain

sealed interface GoalProgress {
    data object NotSet : GoalProgress

    data class OnTrack(val usedMs: Long, val goalMs: Long) : GoalProgress {
        val remainingMs: Long get() = goalMs - usedMs
    }

    data class NearLimit(val usedMs: Long, val goalMs: Long) : GoalProgress {
        val remainingMs: Long get() = goalMs - usedMs
    }

    data class Exceeded(val usedMs: Long, val goalMs: Long) : GoalProgress {
        val exceededMs: Long get() = usedMs - goalMs
    }



}