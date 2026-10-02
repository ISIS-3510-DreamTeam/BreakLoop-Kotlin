package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.DailyUsageStats

class CalculateBaselineUseCase{

    fun calculate(
        days: List<DailyUsageStats>
    ): Long? {
        val values = days.filter { !it.isPartial }.map { it.screenTimeMs }.sorted()
        if (values.isEmpty()){
            return null
        } else if (values.size %2 ==1) {
            return values[values.size / 2]
        } else {
            return (values[values.size / 2] + values[values.size / 2 - 1]) / 2
        }
    }
}