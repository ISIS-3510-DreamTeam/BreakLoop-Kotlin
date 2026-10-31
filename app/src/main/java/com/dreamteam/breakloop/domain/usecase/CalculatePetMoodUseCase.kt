package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.PetMood

class CalculatePetMoodUseCase {

    fun calculate(xpToday: Int): PetMood {
        return if (xpToday >= DAILY_XP_GOAL) PetMood.HAPPY else PetMood.SAD
    }

    companion object {
        // e.g. one 25-min focus session + one short offline activity
        const val DAILY_XP_GOAL = 30
    }
}