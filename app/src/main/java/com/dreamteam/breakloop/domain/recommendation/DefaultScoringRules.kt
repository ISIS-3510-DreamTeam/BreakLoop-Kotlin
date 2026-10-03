package com.dreamteam.breakloop.domain.recommendation

object DefaultScoringRules {
    val all = listOf(
        DurationRule(),
        TimeOfDayRule(),
        WeatherRule(),
        InterestRule(),
        RecentRepeatRule()
    )
}