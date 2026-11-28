package com.dreamteam.breakloop.domain

import java.time.LocalDate

/** How an achievement is unlocked. Same names as the backend enum (AchievementRuleType). */
enum class AchievementRule {
    FIRST_DAY_UNDER_LIMIT,
    STREAK_DAYS,
    FOCUS_SESSIONS_COMPLETED,
    DAYS_UNDER_80_IN_A_ROW,
}

/** One entry of the achievements catalog. image is a path relative to the backend root. */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val rule: AchievementRule,
    val target: Int,
)

/** Progress of one achievement for the current user. */
data class AchievementProgress(
    val achievement: Achievement,
    val current: Int,
    val unlocked: Boolean,
    val unlockedAt: Long? = null, // epoch millis, null while locked
    val synced: Boolean = false,  // true when the backend already has this unlock
) {
    val fraction: Float
        get() = if (achievement.target <= 0) 0f
        else (current.toFloat() / achievement.target).coerceIn(0f, 1f)
}

/** Screen time of one complete past day, in minutes. */
data class DayUsage(val date: LocalDate, val minutes: Int)

/** Everything the rules need, gathered from Room and DataStore. */
data class AchievementInput(
    val days: List<DayUsage>,
    val dailyLimitMinutes: Int?,
    val streak: Int,
    val completedFocusSessions: Int,
)

/** Where the catalog came from, so the views can show their own offline message. */
enum class CatalogSource { NETWORK, SAVED, BUNDLED }

data class AchievementCatalog(
    val achievements: List<Achievement>,
    val source: CatalogSource,
)