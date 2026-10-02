package com.dreamteam.breakloop

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Timestamp
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirestoreUserTest : FirebaseTestSetup() {

    @Test
    fun crearUsuarioDebeCrearDocumentoEnFirestore() = runBlocking {

        val email = "test_${System.currentTimeMillis()}@breakloop.test"
        val password = "Test1234!"

        // 1. Crear usuario en Firebase Authentication
        val authResult = auth
            .createUserWithEmailAndPassword(email, password)
            .await()

        val firebaseUser = authResult.user

        assertNotNull(firebaseUser)

        val uid = firebaseUser!!.uid

        // 2. Crear documento del usuario en Firestore
        val userData = hashMapOf(
            "email" to email,
            "createdAt" to Timestamp.now(),
            "updatedAt" to Timestamp.now()
        )

        firestore
            .collection("users")
            .document(uid)
            .set(userData)
            .await()

        // 3. Leer nuevamente el documento
        val document = firestore
            .collection("users")
            .document(uid)
            .get()
            .await()

        // 4. Verificar que fue creado correctamente
        assertTrue(document.exists())
        assertEquals(email, document.getString("email"))
        assertNotNull(document.getTimestamp("createdAt"))
        assertNotNull(document.getTimestamp("updatedAt"))
    }
}