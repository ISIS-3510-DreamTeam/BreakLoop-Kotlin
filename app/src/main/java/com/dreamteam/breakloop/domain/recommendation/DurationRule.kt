package com.dreamteam.breakloop.domain.recommendation

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ReasonTag

class DurationRule (
    override val weight: Double = 0.25,
    override val reason: ReasonTag? = ReasonTag.FITS_AVAILABLE_TIME
) : ScoringRule {
    override fun score(activity: OfflineActivity, context: ContextSnapshot): Double {
        return if (activity.durationMin <= context.availableMin) {
            activity.durationMin.toDouble() / context.availableMin
        } else {
            0.0
        }
    }
}