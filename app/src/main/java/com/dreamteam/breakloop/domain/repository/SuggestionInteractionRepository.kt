package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.InteractionOutcome
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import com.dreamteam.breakloop.domain.enums.ReasonTag
import com.google.api.Context

interface SuggestionInteractionRepository {

    suspend fun openInteraction(context: ContextSnapshot, now : Long, placeLabel: PlaceLabel?):String

    suspend fun addShownSuggestion(id: String, activityId: String, category: ActivityCategory, availableMin: Int, score: Double, reasons: List<ReasonTag>)

    suspend fun closeInteraction(id: String, outcome: InteractionOutcome, acceptedActivityId: String?, chosenActivityId: String? )

    suspend fun updatePlace(id: String, placeLabel: PlaceLabel?)

    suspend fun syncUnsynced()

}