package com.dreamteam.breakloop.data.remote.mapper

import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import com.dreamteam.breakloop.data.remote.dto.ActivityLogDto
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