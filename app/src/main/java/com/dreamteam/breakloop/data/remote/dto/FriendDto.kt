package com.dreamteam.breakloop.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FriendDto(
    val uid: String,
    val displayName: String,
    val streak: Int,
    val petLevel: Int,
) {
}