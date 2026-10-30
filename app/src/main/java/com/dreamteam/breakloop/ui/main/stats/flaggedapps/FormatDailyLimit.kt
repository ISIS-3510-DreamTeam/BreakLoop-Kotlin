package com.dreamteam.breakloop.ui.main.stats.flaggedapps

internal fun formatDailyLimit(minutes: Int?): String {
    if (minutes == null || minutes <= 0) return "No limit"
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours == 0 -> "$mins min"
        mins == 0 -> "$hours h"
        else -> "$hours h $mins min"
    }
}
