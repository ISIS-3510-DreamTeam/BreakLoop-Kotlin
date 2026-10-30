package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.AddFriendRequestDto
import com.dreamteam.breakloop.data.remote.dto.AddFriendResponseDto
import com.dreamteam.breakloop.data.remote.dto.FriendDto
import com.dreamteam.breakloop.data.remote.dto.UserProfileDto
import com.google.firebase.auth.FirebaseAuth

class FriendsRemoteDataSource(
    private val api: FriendsApi,
    private val auth: FirebaseAuth
) {
    suspend fun getFriends(): List<FriendDto> {
        val (uid, authHeader) = auth.authorized()

        return api.getFriends(uid, authHeader)
    }
    suspend fun postFriendProfile(): UserProfileDto {
        val (uid, authHeader) = auth.authorized()

        return api.postFriendProfile(uid, authHeader)
    }

    suspend fun addFriend(code: String): AddFriendResponseDto {
        val (uid, authHeader) = auth.authorized()
        return api.addFriend(uid, authHeader, AddFriendRequestDto(code))
    }
}