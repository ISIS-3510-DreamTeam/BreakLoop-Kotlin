package com.dreamteam.breakloop

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dreamteam.breakloop.data.sync.FirebaseSyncService
import com.dreamteam.breakloop.data.sync.ProgressData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirestoreProgressTest : FirebaseTestSetup() {

    @Test
    fun guardarProgressDebeCrearDocumentoCorrectamente() = runBlocking {

        // Arrange
        val email =
            "progress_${System.currentTimeMillis()}@breakloop.test"

        val password = "Test1234!"

        auth
            .createUserWithEmailAndPassword(email, password)
            .await()

        val progress = ProgressData(
            xp = 120,
            streak = 5,
            petLevel = 3,
            petHealth = 85
        )

        val syncService = FirebaseSyncService(
            auth = auth,
            firestore = firestore
        )

        // Act
        syncService.saveProgress(progress)

        // Assert
        val uid = auth.currentUser?.uid

        assertNotNull(uid)

        val document = firestore
            .collection("users")
            .document(uid!!)
            .collection("progress")
            .document("current")
            .get()
            .await()

        assertTrue(document.exists())

        assertEquals(120L, document.getLong("xp"))
        assertEquals(5L, document.getLong("streak"))
        assertEquals(3L, document.getLong("petLevel"))
        assertEquals(85L, document.getLong("petHealth"))
    }
}