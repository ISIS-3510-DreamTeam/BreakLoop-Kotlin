package com.dreamteam.breakloop.domain.recommendation

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ReasonTag

class WeatherRule(
    override val weight: Double = 0.2,
    override val reason: ReasonTag? = ReasonTag.GOOD_WEATHER
): ScoringRule {
    override fun score(activity: OfflineActivity, context: ContextSnapshot): Double {
        return if (context.weather in activity.suitableWeather ) {
            1.0
        } else if (context.weather == null) {
            0.5
        } else {
            0.0
        }
    }
}