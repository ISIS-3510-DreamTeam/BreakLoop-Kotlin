package com.dreamteam.breakloop.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequestDto(
    val displayName: String? = null,
    val photoUrl: String? = null
)
