package com.dreamteam.breakloop.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileViewRequestDto(
    val viewedAt: Long
) {
}