package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.StreakSimulationInput
import com.dreamteam.breakloop.domain.StreakSimulationResult
import kotlin.random.Random

/**
 * Monte Carlo simulation: "if my next 30 days look like my past days,
 * how likely am I to get a 7-day streak with each possible goal?"
 * Pure Kotlin (no Android, no I/O): it can run on any thread and be unit-tested.
 */
class StreakSimulator {

    fun run(input: StreakSimulationInput): StreakSimulationResult {
        require(input.minutes.size == input.weekdays.size)

        if (input.minutes.isEmpty()) {
            return StreakSimulationResult.empty(input.minGoal, input.maxGoal, input.step)
        }

        val random = if (input.seed == null) Random.Default else Random(input.seed)

        // 1. Group past days by weekday: byWeekday[1] = all Mondays ... byWeekday[7] = all Sundays
        val byWeekday = List(8) { mutableListOf<Int>() }
        for (i in input.minutes.indices) {
            byWeekday[input.weekdays[i]].add(input.minutes[i])
        }

        // 2. If a weekday has too few samples, use the whole history for it
        val pools = List(8) { w ->
            if (byWeekday[w].size >= input.minSamplesPerWeekday) byWeekday[w] else input.minutes
        }

        // 3. Pool for each future day: day 0 is startWeekday, then the following weekdays in order
        val dayPools = List(input.horizonDays) { d -> pools[(input.startWeekday - 1 + d) % 7 + 1] }

        val goalCount = (input.maxGoal - input.minGoal) / input.step + 1
        val histogram = IntArray(goalCount)        // histogram[i] = months whose minimum goal falls in bucket i
        val trajectory = IntArray(input.horizonDays)
        val lastStart = input.horizonDays - input.streakLength

        // 4. Invent many possible months
        repeat(input.simulations) {
            // 4a. Each future day takes a random real value from its pool
            for (d in 0 until input.horizonDays) {
                val pool = dayPools[d]
                trajectory[d] = pool[random.nextInt(pool.size)]
            }

            // 4b. Lowest goal that gives a 7-day streak in this month:
            // in each 7-day window the goal must cover the worst day (the max),
            // and the best window is the one whose max is smallest.
            var threshold = Int.MAX_VALUE
            for (start in 0..lastStart) {
                var windowMax = 0
                for (k in 0 until input.streakLength) {
                    val v = trajectory[start + k]
                    if (v > windowMax) {
                        windowMax = v
                        if (windowMax >= threshold) break // this window can't beat the best one
                    }
                }
                if (windowMax < threshold) threshold = windowMax
            }

            // 4c. Count it. Months that would need more than maxGoal count as "no streak".
            if (threshold <= input.maxGoal) {
                val index = if (threshold <= input.minGoal) 0
                else (threshold - input.minGoal + input.step - 1) / input.step // rounded up
                histogram[index]++
            }
        }

        // 5. P(goal) = fraction of months where that goal (or a lower one) was enough.
        //    Suggested = lowest goal with P >= targetProbability.
        val probabilities = MutableList(goalCount) { 0.0 }
        var cumulative = 0
        var suggested: Int? = null
        for (i in 0 until goalCount) {
            cumulative += histogram[i]
            probabilities[i] = cumulative.toDouble() / input.simulations
            if (suggested == null && probabilities[i] >= input.targetProbability) {
                suggested = input.minGoal + i * input.step
            }
        }

        return StreakSimulationResult(input.minGoal, input.step, probabilities, input.simulations, suggested)
    }
}