package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.FocusSessionRequest
import kotlinx.coroutines.runBlocking

fun testFocusApi() = runBlocking {

    val session = FocusSessionRequest(
        id = "test-session-001",
        startTime = System.currentTimeMillis(),
        duration = 25,
        type = "FOCUS",
        status = "COMPLETED",
        xpEarned = 0
    )

    val response = RetrofitInstance.focusApi.saveSession(
        uid = "test-user",
        session = session
    )

    println("HTTP CODE: ${response.code()}")
    println("SUCCESS: ${response.isSuccessful}")
}