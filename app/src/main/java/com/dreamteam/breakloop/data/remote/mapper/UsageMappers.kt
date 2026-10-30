package com.dreamteam.breakloop.data.remote.mapper

import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import com.dreamteam.breakloop.data.local.entity.SuggestionInteractionEntity
import com.dreamteam.breakloop.data.remote.dto.ActivityLogDto
import com.dreamteam.breakloop.data.remote.dto.ShownSuggestionDto
import com.dreamteam.breakloop.data.remote.dto.SuggestionInteractionDto
import kotlinx.serialization.json.Json
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

fun SuggestionInteractionEntity.toDto(): SuggestionInteractionDto {
    val shownSuggestions = try {
        Json.decodeFromString<List<ShownSuggestionDto>>(shownSuggestionsJson)
    } catch (_: Exception) {
        emptyList()
    }
    return SuggestionInteractionDto(
        id = id,
        openedAt = openedAt,
        closedAt = closedAt,
        timeOfDay = timeOfDay,
        isWeekend = isWeekend,
        weather = weather,
        placeLabel = placeLabel,
        latitude = latitude,
        longitude = longitude,
        shownSuggestions = shownSuggestions,
        outcome = outcome,
        acceptedActivityId = acceptedActivityId,
        chosenActivityId = chosenActivityId,
        declineReason = null
    )
}

fun SuggestionInteractionDto.toEntity(): SuggestionInteractionEntity {
    val jsonString = Json.encodeToString(shownSuggestions)
    return SuggestionInteractionEntity(
        id = id,
        openedAt = openedAt,
        closedAt = closedAt,
        timeOfDay = timeOfDay,
        isWeekend = isWeekend,
        weather = weather,
        placeLabel = placeLabel,
        latitude = latitude?.let { round(it * 100.0) / 100.0 },
        longitude = longitude?.let { round(it * 100.0) / 100.0 },
        shownSuggestionsJson = jsonString,
        outcome = outcome,
        acceptedActivityId = acceptedActivityId,
        chosenActivityId = chosenActivityId,
        updatedAt = System.currentTimeMillis(),
        synced = false
    )
}