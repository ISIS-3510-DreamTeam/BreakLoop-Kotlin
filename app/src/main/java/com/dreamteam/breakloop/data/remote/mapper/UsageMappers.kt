package com.dreamteam.breakloop.data.remote.mapper

import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import com.dreamteam.breakloop.data.local.entity.DailyUsageStatsEntity
import com.dreamteam.breakloop.data.remote.dto.ActivityLogDto
import com.dreamteam.breakloop.data.remote.dto.UsageDto
import kotlin.math.round

fun ActivityLogEntity.toDto(): ActivityLogDto {
    return ActivityLogDto(
        id = id,
        activityId = activityId,
        category = category,
        source = source,
        interactionId = interactionId,
        startedAt = startedAt,
        completedAt = completedAt,
        xp = xp,
        timeOfDay = timeOfDay,
        weather = weather,
        availableMin = availableMin,
        placeLabel = placeLabel,
        latitude = latitude,
        longitude = longitude
    )
}
fun ActivityLogDto.toEntity(): ActivityLogEntity {
    return ActivityLogEntity(
        id = id,
        activityId = activityId,
        category = category,
        source = source,
        interactionId = interactionId,
        startedAt = startedAt,
        completedAt = completedAt,
        xp = xp,
        timeOfDay = timeOfDay,
        weather = weather,
        availableMin = availableMin,
        placeLabel = placeLabel,
        latitude = latitude?.let { round(it * 100.0) / 100.0 },
        longitude = longitude?.let { round(it * 100.0) / 100.0 }
    )
}

fun DailyUsageStatsEntity.toDto(): UsageDto {
    return UsageDto(
        date = date,
        screenTimeMs = screenTimeMs,
        pickups = pickups,
        unlocks = unlocks,
        isPartial = isPartial,
        updatedAt = updatedAt,
        flaggedTimeMs = flaggedTimeMs
    )
}

fun UsageDto.toEntity(synced: Boolean = true): DailyUsageStatsEntity {
    return DailyUsageStatsEntity(
        date = date,
        screenTimeMs = screenTimeMs,
        pickups = pickups,
        unlocks = unlocks,
        isPartial = isPartial,
        updatedAt = updatedAt,
        flaggedTimeMs = flaggedTimeMs,
        synced = synced
    )
}