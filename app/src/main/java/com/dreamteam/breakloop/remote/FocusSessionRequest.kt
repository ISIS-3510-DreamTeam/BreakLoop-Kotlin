package com.dreamteam.breakloop.remote

import kotlinx.serialization.Serializable

@Serializable
data class FocusSessionRequest(
    val id: String,
    val startTime: Long,
    val duration: Int,
    val type: String,
    val status: String,
    val xpEarned: Int
)