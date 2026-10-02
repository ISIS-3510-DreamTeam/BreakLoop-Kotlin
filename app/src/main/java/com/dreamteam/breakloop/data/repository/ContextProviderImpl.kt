package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.system.InterestsDataSource
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import com.dreamteam.breakloop.domain.repository.ContextProvider
import java.time.Clock
import java.time.LocalDateTime

class ContextProviderImpl(
    private val interestsDataSource: InterestsDataSource,
    private val clock: Clock = Clock.systemDefaultZone()
): ContextProvider {
    override suspend fun getSnapshot(availableMin: Int): ContextSnapshot {
        val now = LocalDateTime.now(clock)
        return ContextSnapshot(
            timeOfDay = TimeOfDay.fromHour(now.hour),
            isWeekend = now.dayOfWeek.value > 5,
            availableMin = availableMin,
            weather = null, // TODO: más adelante lo conecto
            interests = interestsDataSource.getInterests(),
            recentActivityIds = emptyList() // TODO: más adelante
        )
    }
}