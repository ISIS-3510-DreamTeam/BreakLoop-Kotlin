package com.dreamteam.breakloop.data.remote

import com.dreamteam.breakloop.data.remote.dto.SuggestionInteractionDto
import com.google.firebase.auth.FirebaseAuth

class SuggestionInteractionRemoteDataSource(
    private val api: SuggestionInteractionApi,
    private val auth: FirebaseAuth

) {
    suspend fun upload(suggestionInteractions: List<SuggestionInteractionDto>) {
        val (uid, authHeader) = auth.authorized()

        api.postSuggestionInteraction(
            uid,
            authHeader,
            suggestionInteractions
        )

    }
}