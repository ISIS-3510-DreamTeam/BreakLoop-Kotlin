package com.dreamteam.breakloop.domain.enums

enum class TimeOfDay {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT;

    companion object {
        fun fromHour(hour: Int): TimeOfDay {
            return when (hour) {
                in 6..11 -> MORNING
                in 12..17 -> AFTERNOON
                in 18..22 -> EVENING
                else -> NIGHT
            }
        }
    }
}