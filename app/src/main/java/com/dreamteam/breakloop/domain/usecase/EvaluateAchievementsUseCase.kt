package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.Achievement
import com.dreamteam.breakloop.domain.AchievementInput
import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.domain.AchievementRule
import java.time.LocalDate

/**
 * Rule engine of the achievements. Pure Kotlin (no Android, no I/O),
 * so it can run on any thread and be unit-tested. Same rules as the Flutter app.
 */
class EvaluateAchievementsUseCase {

    fun evaluate(catalog: List<Achievement>, input: AchievementInput): List<AchievementProgress> =
        catalog.map { achievement ->
            val current = currentFor(achievement, input)
            AchievementProgress(
                achievement = achievement,
                current = current,
                unlocked = current >= achievement.target,
            )
        }

    private fun currentFor(achievement: Achievement, input: AchievementInput): Int =
        when (achievement.rule) {
            // The streak only counts finished days, so a good morning today does not unlock it yet
            AchievementRule.FIRST_DAY_UNDER_LIMIT -> if (input.streak >= 1) 1 else 0
            AchievementRule.STREAK_DAYS -> input.streak
            AchievementRule.FOCUS_SESSIONS_COMPLETED -> input.completedFocusSessions
            AchievementRule.DAYS_UNDER_80_IN_A_ROW -> longestRunUnder80(input)
        }

    /** Longest run of consecutive days using less than 80% of the daily limit. */
    private fun longestRunUnder80(input: AchievementInput): Int {
        val limit = input.dailyLimitMinutes
        if (limit == null || limit <= 0) return 0

        var best = 0
        var run = 0
        var previous: LocalDate? = null

        for (day in input.days.sortedBy { it.date }) {
            val consecutive = previous != null && day.date == previous.plusDays(1)
            val under = day.minutes * 5 < limit * 4 // minutes < 80% of the limit, without decimals

            run = if (under) (if (consecutive) run + 1 else 1) else 0
            if (run > best) best = run
            previous = day.date
        }
        return best
    }
}