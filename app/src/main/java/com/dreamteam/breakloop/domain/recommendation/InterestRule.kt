package com.dreamteam.breakloop.domain.recommendation

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ReasonTag

class InterestRule(
    override val weight: Double = 0.35,
    override val reason: ReasonTag? = ReasonTag.MATCHES_INTEREST
) : ScoringRule {
    override fun score(activity: OfflineActivity, context: ContextSnapshot): Double {
        return if (activity.category in context.interests) {
            1.0
        } else {
            0.5
        }
    }
}