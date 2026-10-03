package com.dreamteam.breakloop.domain.recommendation

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ReasonTag

class TimeOfDayRule (
    override val weight: Double = 0.20,
    override val reason: ReasonTag? = ReasonTag.RIGHT_TIME_OF_DAY
) : ScoringRule {
    override fun score(activity: OfflineActivity, context: ContextSnapshot): Double {
        return if (context.timeOfDay in activity.suitableTimesOfDay ) {
            1.0
        } else {
            0.0
        }
    }
}