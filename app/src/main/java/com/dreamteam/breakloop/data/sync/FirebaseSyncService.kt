package com.dreamteam.breakloop.data.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseSyncService (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : SyncService {

    override suspend fun saveProgress(progress: ProgressData) {
        val uid = auth.currentUser?.uid
            ?:throw IllegalStateException("No authenticated user")

        firestore
            .collection("users")
            .document(uid)
            .collection("progress")
            .document("current")
            .set(
                mapOf(
                    "xp" to progress.xp,
                    "streak" to progress.streak,
                    "petLevel" to progress.petLevel,
                    "petHealth" to progress.petHealth
                )
            )
            .await()
    }


    override suspend fun restoreProgress(): ProgressData? {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("No authenticated user")

        val document = firestore
            .collection("users")
            .document(uid)
            .collection("progress")
            .document("current")
            .get()
            .await()

        if (!document.exists()){
            return null
        }

        return ProgressData(
            xp = document.getLong("xp")?.toInt() ?: 0,
            streak = document.getLong("streak")?.toInt() ?: 0,
            petLevel = document.getLong("petLevel")?.toInt() ?: 1,
            petHealth = document.getLong("petHealth")?.toInt() ?: 100
        )

    }

}