package com.dreamteam.breakloop.domain.recommendation

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ReasonTag

interface ScoringRule{
    val weight: Double
    val reason: ReasonTag?
    fun score (activity: OfflineActivity, context: ContextSnapshot): Double
}