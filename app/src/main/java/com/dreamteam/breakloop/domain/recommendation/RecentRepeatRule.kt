package com.dreamteam.breakloop.domain.recommendation

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ReasonTag

class RecentRepeatRule(
    override val weight: Double = -0.50,
    override val reason: ReasonTag? = null
) : ScoringRule {
    override fun score(activity: OfflineActivity, context: ContextSnapshot): Double {
        return if (activity.id in context.recentActivityIds) {
            1.0
        } else {
            0.0
        }
    }
}