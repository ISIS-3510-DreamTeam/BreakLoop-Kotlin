package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.ActivityLogDto
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GetTokenResult
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ActivityLogApi {

    @PUT("api/users/{uid}/activity-logs/{logId}")
    suspend fun putActivityLog(
        @Path("uid") uid: String,
        @Path("logId") logId: String,
        @Header("Authorization") authorization: String,
        @Body request: ActivityLogDto
    ): Unit

    @POST("api/users/{uid}/activity-logs/batch")
    suspend fun postActivityLogs(
        @Path("uid") uid: String,
        @Header("Authorization") token: String,
        @Body request: List<ActivityLogDto>
        ): Unit
}