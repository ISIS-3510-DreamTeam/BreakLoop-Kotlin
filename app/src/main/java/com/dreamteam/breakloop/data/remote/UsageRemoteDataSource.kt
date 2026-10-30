package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.UsageDto
import com.google.firebase.auth.FirebaseAuth

class UsageRemoteDataSource(
    private val api: UsageApi,
    private val auth: FirebaseAuth
) {
    suspend fun post(usage: UsageDto){
        val (uid, authHeader) = auth.authorized()
        api.postUsage(
            uid,
            authHeader,
            usage
        )
    }
}