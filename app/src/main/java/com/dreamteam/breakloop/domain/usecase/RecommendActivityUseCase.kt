package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.Recommendation
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import com.dreamteam.breakloop.domain.enums.WeatherCondition
import com.dreamteam.breakloop.domain.recommendation.ScoringRule

class RecommendActivityUseCase(
    private val rules: List<ScoringRule>
) {
    fun rank(catalog: List<OfflineActivity>, context: ContextSnapshot): List<Recommendation> {
        val reccomendations: MutableList<Recommendation> = mutableListOf()
        val candidates = catalog.filter { activity -> activity.durationMin <= context.availableMin && !(activity.isOutdoor && (context.weather == WeatherCondition.RAIN || context.weather ==WeatherCondition.SNOW || context.timeOfDay == TimeOfDay.NIGHT)) }
        for( candidate in candidates){
            val scores = rules.sumOf { rule -> rule.score(candidate, context)*rule.weight }
            val reasons = rules.filter { rule -> rule.score(candidate, context) ==1.0 }.map { rule -> rule.reason }
            val recommendation = Recommendation(
                activity = candidate,
                score = scores,
                reasons = reasons
            )
            reccomendations.add(recommendation)
        }
        return reccomendations.sortedByDescending { it.score }
    }

    fun recommend(catalog: List<OfflineActivity>, contextSnapshot: ContextSnapshot): Recommendation? {
        val rank = rank(catalog, contextSnapshot)
        if (rank.isNotEmpty()) {
            return rank.get(0)
        } else{
            return null
        }
    }
}