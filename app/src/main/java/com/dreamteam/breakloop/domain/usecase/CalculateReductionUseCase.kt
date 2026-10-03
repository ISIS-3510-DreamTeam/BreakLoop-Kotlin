package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.ReductionProgress

class CalculateReductionUseCase {
    fun calculate(baselineMs: Long?, weeklyAverageMs: Long?): ReductionProgress? {
        if (baselineMs==null || baselineMs == 0L || weeklyAverageMs== null) {
            return null
        }
        val changePercent = ((weeklyAverageMs.toFloat() - baselineMs) / baselineMs * 100).toInt()
        val progress = (-changePercent / 20f).coerceIn(0f,1f)

        return ReductionProgress( baselineMs, changePercent, progress)
    }
}