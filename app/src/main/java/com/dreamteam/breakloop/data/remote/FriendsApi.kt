package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.AddFriendRequestDto
import com.dreamteam.breakloop.data.remote.dto.AddFriendResponseDto
import com.dreamteam.breakloop.data.remote.dto.FriendDto
import com.dreamteam.breakloop.data.remote.dto.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface FriendsApi {

    @POST("api/users/{uid}/friend-profile")
    suspend fun postFriendProfile(
        @Path("uid") uid: String,
        @Header("Authorization") token: String,
    ): UserProfileDto

    @POST("api/users/{uid}/friends")
    suspend fun addFriend(
        @Path("uid") uid: String,
        @Header("Authorization") token: String,
        @Body request: AddFriendRequestDto
    ): AddFriendResponseDto

    @GET("api/users/{uid}/friends")
    suspend fun getFriends(
        @Path("uid") uid: String,
        @Header("Authorization") token: String
    ): List<FriendDto>
}