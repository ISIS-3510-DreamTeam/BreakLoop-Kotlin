package com.dreamteam.breakloop.data.goal

import com.dreamteam.breakloop.domain.DailyUsageStats
import com.dreamteam.breakloop.domain.StreakSimulationInput
import com.dreamteam.breakloop.domain.StreakSimulationResult
import com.dreamteam.breakloop.domain.usecase.StreakSimulator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class StreakSimulationService(
    private val simulator: StreakSimulator = StreakSimulator()
) {

    suspend fun simulate(days: List<DailyUsageStats>, today: LocalDate = LocalDate.now()): StreakSimulationResult {
        // Only complete past days (today is not past)
        val past = days.filter { !it.isPartial && LocalDate.parse(it.date).isBefore(today) }

        val input = StreakSimulationInput(
            minutes = past.map { (it.screenTimeMs / 60_000).toInt() },
            weekdays = past.map { LocalDate.parse(it.date).dayOfWeek.value }, // 1 = Monday, 7 = Sunday
            startWeekday = today.plusDays(1).dayOfWeek.value,
        )

        //  (run in Dispatchers.Default (threads)
        return withContext(Dispatchers.Default) { simulator.run(input) }
    }
}