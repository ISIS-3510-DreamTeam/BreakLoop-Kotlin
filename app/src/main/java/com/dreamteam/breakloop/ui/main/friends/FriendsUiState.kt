package com.dreamteam.breakloop.ui.main.friends

import com.dreamteam.breakloop.domain.Friend

data class FriendsUiState(
        val myFriendCode: String?,
        val friends: List<Friend>,
        val lastUpdated: Long?,
        val pendingCodes: Set<String>,
        val isOffline: Boolean,
        val isLoading: Boolean,
        val message: String?
)
