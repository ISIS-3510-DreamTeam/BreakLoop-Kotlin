package com.dreamteam.breakloop.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShownSuggestionDto (
    val activityId:   String,
    val category:     String,
    val availableMin: Int,
    val score:        Double,
    val reasons:      List<String>,
    val shownAt:      Long,

)