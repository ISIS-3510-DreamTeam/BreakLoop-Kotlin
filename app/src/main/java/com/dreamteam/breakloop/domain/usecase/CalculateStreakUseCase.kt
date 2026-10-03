package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.DailyUsageStats
import java.time.LocalDate

class CalculateStreakUseCase {

    fun calculate(days: List<DailyUsageStats>, goalMs: Long?, today: LocalDate): Int {
        if (goalMs == null || goalMs <= 0) { return 0}

        val byDate = days.associateBy { it.date }   // lookup date

        val todayST = byDate[today.toString()]?.screenTimeMs ?: 0L

        if ((byDate[today.toString()]?.isPartial ?: true)|| todayST > goalMs) { return 0}

        var streak = 0
        var date = today.minusDays(1)
        while (true) {
            val day = byDate[date.toString()] ?: break
            if (day.screenTimeMs > goalMs) { break}
            streak++
            date = date.minusDays(1)
        }
        return streak
    }
}