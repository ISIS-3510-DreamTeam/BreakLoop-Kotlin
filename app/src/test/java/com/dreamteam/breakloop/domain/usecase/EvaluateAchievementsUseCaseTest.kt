package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.Achievement
import com.dreamteam.breakloop.domain.AchievementInput
import com.dreamteam.breakloop.domain.AchievementRule
import com.dreamteam.breakloop.domain.DayUsage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** Same cases as Hernán's evaluate_achievements_test.dart in the Flutter app. */
class EvaluateAchievementsUseCaseTest {

    private val useCase = EvaluateAchievementsUseCase()
    private val start = LocalDate.of(2026, 11, 1)

    private fun achievement(rule: AchievementRule, target: Int) =
        Achievement(id = rule.name, name = "", description = "", image = "", rule = rule, target = target)

    private fun day(offset: Long, minutes: Int) = DayUsage(start.plusDays(offset), minutes)

    private fun input(
        days: List<DayUsage> = emptyList(),
        limit: Int? = 100,
        streak: Int = 0,
        focus: Int = 0,
    ) = AchievementInput(days, limit, streak, focus)

    @Test
    fun firstDayUnlocksWhenTheStreakReachesOne() {
        val catalog = listOf(achievement(AchievementRule.FIRST_DAY_UNDER_LIMIT, 1))
        assertFalse(useCase.evaluate(catalog, input(streak = 0)).single().unlocked)
        assertTrue(useCase.evaluate(catalog, input(streak = 1)).single().unlocked)
    }

    @Test
    fun streakUnlocksAtTheTargetNotBefore() {
        val catalog = listOf(achievement(AchievementRule.STREAK_DAYS, 7))
        assertFalse(useCase.evaluate(catalog, input(streak = 6)).single().unlocked)
        assertTrue(useCase.evaluate(catalog, input(streak = 7)).single().unlocked)
    }

    @Test
    fun eightyPercentBoundary() {
        val catalog = listOf(achievement(AchievementRule.DAYS_UNDER_80_IN_A_ROW, 1))
        assertTrue(useCase.evaluate(catalog, input(days = listOf(day(0, 79)))).single().unlocked)
        assertEquals(0, useCase.evaluate(catalog, input(days = listOf(day(0, 80)))).single().current)
    }

    @Test
    fun nonConsecutiveDaysDoNotAddUp() {
        val catalog = listOf(achievement(AchievementRule.DAYS_UNDER_80_IN_A_ROW, 7))
        val days = listOf(day(0, 10), day(1, 10), day(2, 10), day(3, 10), day(5, 10), day(6, 10))
        val result = useCase.evaluate(catalog, input(days = days)).single()
        assertEquals(4, result.current)
        assertFalse(result.unlocked)
    }

    @Test
    fun withoutADailyLimitLimitRulesDoNotUnlock() {
        val catalog = listOf(achievement(AchievementRule.DAYS_UNDER_80_IN_A_ROW, 1))
        assertFalse(useCase.evaluate(catalog, input(days = listOf(day(0, 1)), limit = null)).single().unlocked)
    }

    @Test
    fun focusUnlocksAtTenCompletedSessions() {
        val catalog = listOf(achievement(AchievementRule.FOCUS_SESSIONS_COMPLETED, 10))
        assertFalse(useCase.evaluate(catalog, input(focus = 9)).single().unlocked)
        assertTrue(useCase.evaluate(catalog, input(focus = 10)).single().unlocked)
    }
}