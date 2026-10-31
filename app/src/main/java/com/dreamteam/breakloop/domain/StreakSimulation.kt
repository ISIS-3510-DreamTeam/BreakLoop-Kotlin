package com.dreamteam.breakloop.domain

import kotlin.math.roundToInt

//Montecarlo usa past daily usage
data class StreakSimulationInput(
    val minutes: List<Int>,          // screen time of each past day, in minutes (today not included)
    val weekdays: List<Int>,         // weekday of each of those days: 1 = Monday ... 7 = Sunday
    val startWeekday: Int,           // weekday of the first simulated day (tomorrow)
    val simulations: Int = 10_000,   // how many possible months we invent
    val horizonDays: Int = 30,       // length of each invented month
    val streakLength: Int = 7,       // a streak = 7 days in a row under the goal
    val minGoal: Int = 15,           // goals evaluated: 15, 30, 45 ... 720 minutes
    val maxGoal: Int = 720,
    val step: Int = 15,
    val targetProbability: Double = 0.7, // suggested goal = lowest goal with at least 70% chance
    val minSamplesPerWeekday: Int = 2,
    val seed: Int? = null,           // fixed seed only for tests (same random numbers every time)
)

//Probabilidad de un streak de 7 dias y recomendacion
data class StreakSimulationResult(
    val minGoal: Int,
    val step: Int,
    val probabilities: List<Double>, // probabilities[i] = P(7-day streak) for goal minGoal + i * step
    val simulations: Int,
    val suggestedGoalMinutes: Int?,  // null if no goal reaches the target probability
) {
    val hasData: Boolean get() = simulations > 0

    fun probabilityFor(goalMinutes: Int): Double {
        val index = ((goalMinutes - minGoal).toDouble() / step).roundToInt()
        return probabilities[index.coerceIn(0, probabilities.size - 1)]
    }

    companion object {
        fun empty(minGoal: Int, maxGoal: Int, step: Int): StreakSimulationResult {
            val count = (maxGoal - minGoal) / step + 1
            return StreakSimulationResult(minGoal, step, List(count) { 0.0 }, 0, null)
        }
    }
}