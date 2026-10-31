package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.ActivityLogDto
import com.google.firebase.auth.FirebaseAuth

class ActivityLogRemoteDataSource(
    private val api: ActivityLogApi,
    private val auth: FirebaseAuth
) {
    suspend fun upload(logs: List<ActivityLogDto>){
        val (uid, authHeader) = auth.authorized()
        api.postActivityLogs(
            uid,
            authHeader,
            logs
        )
    }

}