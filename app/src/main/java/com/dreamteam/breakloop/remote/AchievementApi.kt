package com.dreamteam.breakloop.remote

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/** One entry of GET /api/achievements. rule stays a String so an unknown rule never breaks parsing. */
@Serializable
data class AchievementDto(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val rule: String,
    val target: Int,
)

@Serializable
data class AchievementUnlockRequest(
    val unlockedAt: Long, // epoch millis, taken by the phone when the badge was unlocked
)

interface AchievementApi {

    @GET("api/achievements")
    suspend fun getCatalog(): List<AchievementDto>

    /** Idempotent on the backend: a repeated unlock keeps the earliest date. */
    @PUT("api/users/{uid}/achievements/{achievementId}")
    suspend fun unlock(
        @Path("uid") uid: String,
        @Path("achievementId") achievementId: String,
        @Body request: AchievementUnlockRequest,
    ): Response<Unit>
}