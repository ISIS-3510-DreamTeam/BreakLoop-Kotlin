package com.dreamteam.breakloop.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(val current: CurrentWeather) {

}

@Serializable
data class CurrentWeather(@SerialName("weather_code") val weatherCode: Int) {

}