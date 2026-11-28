package com.dreamteam.breakloop.ui.main.profile

import com.dreamteam.breakloop.domain.MyProfile

sealed interface ProfileUiState {
    data object Loading: ProfileUiState
    data object NotLoggedIn: ProfileUiState
    data class Content(
        val profile: MyProfile?,
        val isSaving: Boolean,
        val message: String?
    ) : ProfileUiState
}