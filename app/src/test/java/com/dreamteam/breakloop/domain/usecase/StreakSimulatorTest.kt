package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.StreakSimulationInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakSimulatorTest {

    private val simulator = StreakSimulator()

    @Test
    fun constantUsageOf60Minutes() {
        val r = simulator.run(
            StreakSimulationInput(
                minutes = List(28) { 60 },
                weekdays = List(28) { it % 7 + 1 },
                startWeekday = 1,
                simulations = 2000,
                seed = 42,
            )
        )
        assertEquals(0.0, r.probabilityFor(45), 0.0)
        assertEquals(1.0, r.probabilityFor(60), 0.0)
        assertEquals(60, r.suggestedGoalMinutes)
    }

    @Test
    fun probabilityNeverDecreasesWithGoal() {
        val r = simulator.run(
            StreakSimulationInput(
                minutes = listOf(30, 90, 150, 240, 60, 120, 200, 45, 100, 180),
                weekdays = listOf(1, 2, 3, 4, 5, 6, 7, 1, 2, 3),
                startWeekday = 3,
                simulations = 5000,
                seed = 7,
            )
        )
        for (i in 1 until r.probabilities.size) {
            assertTrue(r.probabilities[i] >= r.probabilities[i - 1])
        }
    }

    @Test
    fun emptyHistory() {
        val r = simulator.run(
            StreakSimulationInput(
                minutes = emptyList(),
                weekdays = emptyList(),
                startWeekday = 1
            )
        )
        assertFalse(r.hasData)
        assertNull(r.suggestedGoalMinutes)
    }
}