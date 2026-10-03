package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.remote.WeatherApi
import com.dreamteam.breakloop.data.system.LocationDataSource
import com.dreamteam.breakloop.domain.enums.WeatherCondition
import com.dreamteam.breakloop.domain.repository.WeatherRepository
import kotlinx.coroutines.withTimeoutOrNull

class WeatherRepositoryImpl(
    private val locationDataSource: LocationDataSource,
    private val weatherApi: WeatherApi
) : WeatherRepository {

    private var cachedCondition: WeatherCondition? = null
    private var cachedAt: Long = 0L

    override suspend fun getCurrentWeather(): WeatherCondition? {
        val isCacheValid = cachedCondition != null &&
            System.currentTimeMillis() - cachedAt < 30 * 60 * 1000
        if (isCacheValid) {
            return cachedCondition
        }

        val coords = locationDataSource.getApproximateLocation() ?: return null

        return try {
            val response = withTimeoutOrNull(1500L) {
                weatherApi.getCurrentWeather(coords.latitude, coords.longitude)
            } ?: return null

            val condition = WeatherCondition.fromWmoCode(response.current.weatherCode)
            cachedCondition = condition
            cachedAt = System.currentTimeMillis()
            condition
        } catch (error: Exception) {
            null
        }
    }
}
