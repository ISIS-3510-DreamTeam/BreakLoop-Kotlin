package com.dreamteam.breakloop.domain

data class FriendProfile(
    val uid: String,
    val displayName: String,
    val photoUrl: String?,
    val streak: Int,
    val petLevel: Int,
    val badges: List<String>,
    val fetchedAt: Long
)

sealed interface FriendProfileResult {
    data class Fresh(val profile: FriendProfile) : FriendProfileResult
    data class Stale(val profile: FriendProfile) : FriendProfileResult
    data object NeverLoaded : FriendProfileResult
}
