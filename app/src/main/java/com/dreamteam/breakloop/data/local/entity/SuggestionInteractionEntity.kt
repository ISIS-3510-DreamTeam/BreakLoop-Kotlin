package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dreamteam.breakloop.domain.enums.InteractionOutcome
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import com.dreamteam.breakloop.domain.enums.TimeOfDay

@Entity(tableName = "suggestion_interaction")
data class SuggestionInteractionEntity (

    @PrimaryKey
    val id : String,
    val openedAt: Long,
    val closedAt : Long?,
    val timeOfDay: TimeOfDay,
    val isWeekend : Boolean,
    val weather: String?,
    val placeLabel: PlaceLabel?,
    val latitude: Double?,
    val longitude: Double?,
    val shownSuggestionsJson: String,
    val outcome : InteractionOutcome,
    val acceptedActivityId : String?,
    val chosenActivityId: String?,
    val updatedAt : Long,
    val synced : Boolean  =false
)