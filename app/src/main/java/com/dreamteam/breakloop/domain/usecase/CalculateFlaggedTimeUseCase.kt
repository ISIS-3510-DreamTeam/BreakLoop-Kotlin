package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.FlaggedApp

class CalculateFlaggedTimeUseCase {
    fun getFlaggedAppTime(dailyAppUsage: List<AppUsage>, labeled: List<FlaggedApp>): Long {
        var flaggedTime: Long = 0
        for (appUsage in dailyAppUsage) {
            if (labeled.any { it.packageName == appUsage.packageName }){
                flaggedTime += appUsage.foregroundMs
            }
        }
        return flaggedTime
    }
}