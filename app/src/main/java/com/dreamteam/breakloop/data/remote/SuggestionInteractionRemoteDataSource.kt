package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.SuggestionInteractionDto
import com.dreamteam.breakloop.data.remote.exception.NoSessionException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class SuggestionInteractionRemoteDataSource(
    private val api: SuggestionInteractionApi,
    private val auth: FirebaseAuth

) {
    suspend fun upload(suggestionInteractions: List<SuggestionInteractionDto>) {
        val user = auth.currentUser ?: throw NoSessionException("User not authenticated")
        val token = user.getIdToken(false).await().token ?: throw NoSessionException("User token not found")

        api.postSuggestionInteraction(
            user.uid,
            "Bearer $token",
            suggestionInteractions
        )

    }
}