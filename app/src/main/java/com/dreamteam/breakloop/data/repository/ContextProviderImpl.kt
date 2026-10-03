package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.system.InterestsDataSource
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import com.dreamteam.breakloop.domain.repository.ActivityLogRepository
import com.dreamteam.breakloop.domain.repository.ContextProvider
import com.dreamteam.breakloop.domain.repository.WeatherRepository
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Clock
import java.time.LocalDateTime

class ContextProviderImpl(
    private val interestsDataSource: InterestsDataSource,
    private val activityLogRepository: ActivityLogRepository,
    private val weatherRepository: WeatherRepository,
    private val clock: Clock = Clock.systemDefaultZone()
): ContextProvider {
    override suspend fun getSnapshot(availableMin: Int): ContextSnapshot {
        val now = LocalDateTime.now(clock)
        val weather = withTimeoutOrNull(2500) { weatherRepository.getCurrentWeather() }
        return ContextSnapshot(
            timeOfDay = TimeOfDay.fromHour(now.hour),
            isWeekend = now.dayOfWeek.value > 5,
            availableMin = availableMin,
            weather = weather,
            interests = interestsDataSource.getInterests(),
            recentActivityIds = activityLogRepository.getRecentActivityIds(3)
        )
    }
}