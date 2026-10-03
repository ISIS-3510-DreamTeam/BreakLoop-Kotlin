package com.dreamteam.breakloop.domain.enums

enum class WeatherCondition {
    CLEAR,
    CLOUDY,
    RAIN,
    SNOW;

    companion object {
        fun fromWmoCode(code: Int): WeatherCondition {
            return when (code) {
                0, 1 -> CLEAR
                2, 3, 45, 48 -> CLOUDY
                in 51..67, in 80..82, in 95..99 -> RAIN
                in 71..77, 85, 86 -> SNOW
                else -> CLOUDY
            }
        }
    }
}
