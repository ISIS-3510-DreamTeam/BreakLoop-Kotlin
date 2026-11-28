package com.dreamteam.breakloop.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PublicProfileDto(
    val uid: String,
    val displayName: String,
    val photoUrl: String? = null,
    val streak: Int,
    val petLevel: Int,

)
