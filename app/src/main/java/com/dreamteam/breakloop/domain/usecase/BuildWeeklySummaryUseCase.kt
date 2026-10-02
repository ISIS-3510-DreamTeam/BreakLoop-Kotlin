package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.DailyUsageStats
import com.dreamteam.breakloop.domain.DayBar
import com.dreamteam.breakloop.domain.WeeklySummary
import java.time.LocalDate

class BuildWeeklySummaryUseCase{
    fun buildSummary(
        thisWeekStats: List<DailyUsageStats>,
        lastWeekStats: List<DailyUsageStats>,
        mondayDate: LocalDate,
        todayDate: String
    ): WeeklySummary {
        val bars = mutableListOf<DayBar>()
        for (i in 0..6) {
            val date = mondayDate.plusDays(i.toLong())
            val dayData = thisWeekStats.firstOrNull { it.date == date.toString() }
            if (dayData != null) {
                val dayBar = DayBar(
                    dayOfWeek = date.dayOfWeek,
                    screenTimeMs = dayData.screenTimeMs
                )
                bars.add(dayBar)
            } else {
                bars.add(DayBar(
                    dayOfWeek = date.dayOfWeek,
                    screenTimeMs = null
                ))
            }
        }
        val averageLastWeek = lastWeekStats.filter{ !it.isPartial}.map { it.screenTimeMs }.average()
        val averageThisWeek = thisWeekStats.filter{ it.date != todayDate && !it.isPartial }.map { it.screenTimeMs }.average()
        val change : Int?
        if(averageLastWeek.isNaN() || averageThisWeek.isNaN()|| averageLastWeek ==0.0){
            change = null
        } else {
            val result = (averageThisWeek - averageLastWeek) / averageLastWeek * 100
            change = result.toInt()
        }
        val weeklySummary = WeeklySummary(
            bars = bars,
            weeklyAverageMs = averageThisWeek.toLong(),
            changeVsLastWeekPercent = change,
            mondayDate = mondayDate
        )
        return weeklySummary
    }


}