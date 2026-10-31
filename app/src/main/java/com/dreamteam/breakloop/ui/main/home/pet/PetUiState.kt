package com.dreamteam.breakloop.ui.main.home.pet

import androidx.compose.ui.graphics.ImageBitmap
import com.dreamteam.breakloop.domain.PetMood
import com.dreamteam.breakloop.domain.usecase.CalculatePetMoodUseCase
import com.dreamteam.breakloop.remote.PetDto

data class PetUiState(
    val currentPetId: String? = null,
    val pendingPetId: String? = null,
    val mood: PetMood = PetMood.SAD,
    val image: ImageBitmap? = null,
    val xpToday: Int = 0,
    val xpGoal: Int = CalculatePetMoodUseCase.DAILY_XP_GOAL,
    val catalog: List<PetDto> = emptyList()
) {
    val hasPet: Boolean get() = currentPetId != null || pendingPetId != null

    fun nameOf(petId: String?): String =
        catalog.find { it.id == petId }?.name ?: petId ?: ""
}