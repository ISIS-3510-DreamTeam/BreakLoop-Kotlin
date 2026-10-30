package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.exception.NoSessionException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/** Devuelve (uid, "Bearer <token>") del usuario actual; lanza [NoSessionException] si no hay sesión. */
internal suspend fun FirebaseAuth.authorized(): Pair<String, String> {
    val user = currentUser ?: throw NoSessionException("User not authenticated")
    val token = user.getIdToken(false).await().token ?: throw NoSessionException("User token not found")
    return user.uid to "Bearer $token"
}
