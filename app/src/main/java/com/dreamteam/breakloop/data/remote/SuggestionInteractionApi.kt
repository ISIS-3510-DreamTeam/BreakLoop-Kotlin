package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.SuggestionInteractionDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface SuggestionInteractionApi {

    @POST("api/users/{uid}/suggestion-interactions/batch")
    suspend fun postSuggestionInteraction(
        @Path("uid") uid: String,
        @Header("Authorization") auth: String,
        @Body request: List<SuggestionInteractionDto>
    ): Unit


}