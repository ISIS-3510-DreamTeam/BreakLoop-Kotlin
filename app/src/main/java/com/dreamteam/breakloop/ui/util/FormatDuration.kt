package com.dreamteam.breakloop.ui.util

fun formatDuration(ms: Long): String {
    val hours = ms / 3600000
    val minutes = (ms % 3600000) / 60000
    if (hours > 0) {
        return "${hours} h ${minutes} min"
    } else {
        return "${ minutes} min"
    }
}