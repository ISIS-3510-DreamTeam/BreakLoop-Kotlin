package com.dreamteam.breakloop.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PhonePickupRemoteDatasource(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    suspend fun saveBatchSummary(
        userId: String,
        batchData: Map<String, Any>
    ) {
        require(userId.isNotEmpty()) {
            "The user ID cannot be empty."
        }

        val batchId = batchData["batchId"] as? String
            ?: throw IllegalArgumentException("batchId is required")

        firestore.collection("users")
            .document(userId)
            .collection("phone_batches")
            .document(batchId)
            .set(batchData)
            .await()
    }
}