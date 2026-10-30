package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.ActivityLogDto
import com.dreamteam.breakloop.data.remote.exception.NoSessionException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class ActivityLogRemoteDataSource(
    private val api: ActivityLogApi,
    private val auth: FirebaseAuth
) {
    suspend fun upload(logs: List<ActivityLogDto>){
        val user = auth.currentUser ?: throw NoSessionException("User not authenticated")
        val token = user.getIdToken(false).await().token ?: throw NoSessionException("User token not found")
        api.postActivityLogs(
            user.uid,
            "Bearer $token",
            logs
        )
    }

}