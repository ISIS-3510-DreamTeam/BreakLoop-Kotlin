package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.PublicProfileDto
import com.dreamteam.breakloop.data.remote.dto.UpdateProfileRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApi {

    @GET("api/users/{uid}/public-profile")
    suspend fun getPublicProfile(
        @Path("uid") uid: String,
        @Header("Authorization") token: String,
    ): PublicProfileDto

    @GET("api/users/{uid}/badges")
    suspend fun getBadgesByUid(
        @Path("uid") uid: String,
        @Header("Authorization") token: String,
    ): List<String>

    @PUT("api/users/me/profile")
    suspend fun updateMyProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequestDto
        ): Unit

}