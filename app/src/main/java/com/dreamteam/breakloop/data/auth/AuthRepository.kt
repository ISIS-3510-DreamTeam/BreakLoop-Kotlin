package com.dreamteam.breakloop.data.auth

interface AuthRepository {

    val currentUser: AuthUser?

    suspend fun signIn(email: String, password: String): Result<AuthUser>

    suspend fun signUp(email: String, password: String): Result<AuthUser>

    suspend fun sendPasswordReset(email: String): Result<Unit>

    fun signOut()

}