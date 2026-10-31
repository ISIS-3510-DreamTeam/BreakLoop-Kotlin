package com.dreamteam.breakloop.remote

import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

@Serializable
data class PetDto(
    val id: String,
    val name: String,
    val images: Map<String, String> // "HAPPY" -> "pets/sprout_happy.png"
)
@Serializable
data class PetStateRequest(
    val activePetId: String,
    val petMood: String,
    val petUpdatedAt: Long
)

interface PetApi {

    @GET("api/pets")
    suspend fun getPets(): List<PetDto>

    @PUT("api/users/{uid}/pet")
    suspend fun updatePetState(
        @Path("uid") uid: String,
        @Body state: PetStateRequest
    ): Response<Unit>

    // Downloads file
    @GET
    suspend fun downloadFile(@Url path: String): ResponseBody
}