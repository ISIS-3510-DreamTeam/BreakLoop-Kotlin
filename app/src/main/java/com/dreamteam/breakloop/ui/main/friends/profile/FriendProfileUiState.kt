package com.dreamteam.breakloop.ui.main.friends.profile

import com.dreamteam.breakloop.domain.FriendProfile

sealed interface FriendProfileUiState {
    data object Loading: FriendProfileUiState
    data object NeverLoadedOffline: FriendProfileUiState
    data object NotFound: FriendProfileUiState
    data class Content (
        val profile: FriendProfile,
        val isStale: Boolean
    ) : FriendProfileUiState
}