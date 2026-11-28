package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.ProfileViewRequestDto
import com.dreamteam.breakloop.data.remote.dto.PublicProfileDto
import com.dreamteam.breakloop.data.remote.dto.UpdateProfileRequestDto
import com.google.firebase.auth.FirebaseAuth

class ProfileRemoteDataSource(
    private val api: ProfileApi,
    private val auth: FirebaseAuth

) {
    suspend fun getPublicProfile(friendUid: String): PublicProfileDto{
        val (uid, authHeader) = auth.authorized()
        return api.getPublicProfile(friendUid, authHeader)
    }

    suspend fun getBadges(friendUid: String): List<String> {
        val (uid, authHeader) = auth.authorized()
        return api.getBadgesByUid(friendUid, authHeader)
    }

    suspend fun updateMyProfile(name: String?, photoUrl: String?){
        val (uid, authHeader) = auth.authorized()
        api.updateMyProfile(authHeader, UpdateProfileRequestDto(name,photoUrl))
    }

    suspend fun logProfileView(viewedAt: Long) {
        val (uid, authHeader) = auth.authorized()
        api.logProfileView(authHeader, ProfileViewRequestDto(viewedAt))
    }
}