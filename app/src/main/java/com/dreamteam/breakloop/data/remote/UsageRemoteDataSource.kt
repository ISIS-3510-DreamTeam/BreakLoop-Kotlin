package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.UsageDto
import com.dreamteam.breakloop.data.remote.exception.NoSessionException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UsageRemoteDataSource(
    private val api: UsageApi,
    private val auth: FirebaseAuth
) {
    suspend fun post(usage: UsageDto){
        val user = auth.currentUser ?: throw NoSessionException("User not authenticated")
        val token = user.getIdToken(false).await().token ?: throw NoSessionException("User token not found")
        api.postUsage(
            user.uid,
            "Bearer $token",
            usage
        )
    }
}