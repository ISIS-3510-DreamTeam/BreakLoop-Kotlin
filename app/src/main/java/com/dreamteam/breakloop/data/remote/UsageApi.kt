package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.UsageDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UsageApi {
    @POST("api/users/{uid}/usage")
    suspend fun postUsage(
        @Path("uid") uid: String,
        @Header("Authorization") token: String,
        @Body usage: UsageDto
    ): Unit
}