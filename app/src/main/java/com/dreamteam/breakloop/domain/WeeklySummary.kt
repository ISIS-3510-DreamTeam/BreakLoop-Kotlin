package com.dreamteam.breakloop.domain

import java.time.LocalDate

data class WeeklySummary(
    val bars: List<DayBar>, // always 7
    val weeklyAverageMs: Long?,
    val changeVsLastWeekPercent: Int?,
    val mondayDate: LocalDate
)