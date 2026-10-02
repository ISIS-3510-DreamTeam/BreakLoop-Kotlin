package com.dreamteam.breakloop.domain

import com.dreamteam.breakloop.domain.enums.ReasonTag

data class Recommendation (
    val activity: OfflineActivity,
    val score: Double,
    val reasons: List<ReasonTag>,
){
}