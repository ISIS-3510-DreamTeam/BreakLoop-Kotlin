package com.dreamteam.breakloop.data.remote.dto

import com.dreamteam.breakloop.domain.enums.InteractionOutcome
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import kotlinx.serialization.Serializable

@Serializable
data class SuggestionInteractionDto(
    val id : String,
    val openedAt: Long,
    val closedAt : Long?,
    val timeOfDay: TimeOfDay,
    val isWeekend : Boolean,
    val weather: String?,
    val placeLabel: PlaceLabel?,
    val latitude: Double?,
    val longitude: Double?,
    val shownSuggestions: List<ShownSuggestionDto>,
    val outcome : InteractionOutcome,
    val acceptedActivityId : String?,
    val chosenActivityId: String?,
    val declineReason: String?,
) {
}