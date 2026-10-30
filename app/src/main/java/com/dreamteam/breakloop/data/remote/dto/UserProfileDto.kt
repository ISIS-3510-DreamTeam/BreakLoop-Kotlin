package com.dreamteam.breakloop.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val uid: String,
    val email: String,
    val displayName: String,
    val friendCode: String,
)
