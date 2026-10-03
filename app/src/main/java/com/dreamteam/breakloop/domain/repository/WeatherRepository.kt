package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.data.remote.WeatherResponse
import com.dreamteam.breakloop.domain.enums.WeatherCondition

interface WeatherRepository {
    suspend fun getCurrentWeather(): WeatherCondition?}