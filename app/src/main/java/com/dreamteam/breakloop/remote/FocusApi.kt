package com.dreamteam.breakloop.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FocusApi {

    @GET("api/users/{uid}/focus-sessions")
    suspend fun getSessions(
        @Path("uid") uid: String
    ): Response<List<FocusSessionRequest>>

    @POST("api/users/{uid}/focus-sessions")
    suspend fun saveSession(
        @Path("uid") uid: String,
        @Body session: FocusSessionRequest
    ): Response<Unit>
}