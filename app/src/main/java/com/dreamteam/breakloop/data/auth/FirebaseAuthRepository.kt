package com.dreamteam.breakloop.data.auth
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class FirebaseAuthRepository (private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthRepository {
    override val currentUser: AuthUser?
        get() = auth.currentUser?.let { AuthUser(it.uid, it.email) }

    private fun Exception.toAuthError(): AuthError = when (this) { //WEAK_PASSWORD, NETWORK, TOO_MANY_REQUESTS, UNKNOWN, EMAIL_IN_USE, INVALID_CREDENTIALS
        is FirebaseAuthWeakPasswordException -> AuthError.WEAK_PASSWORD
        is FirebaseNetworkException -> AuthError.NETWORK
        is FirebaseTooManyRequestsException -> AuthError.TOO_MANY_REQUESTS
        is FirebaseAuthUserCollisionException -> AuthError.EMAIL_IN_USE
        is FirebaseAuthInvalidCredentialsException -> AuthError.INVALID_CREDENTIALS
        is FirebaseAuthInvalidUserException -> AuthError.INVALID_CREDENTIALS
        else -> AuthError.UNKNOWN
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<AuthUser> = try {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val user = authResult.user?.let{ AuthUser(it.uid, it.email) }
            ?: throw AuthException(AuthError.UNKNOWN)
        Result.success(user)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(AuthException(e.toAuthError(), e))
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<AuthUser> = try {
        val newUser = auth.createUserWithEmailAndPassword(email,password).await()
        val user = newUser.user?.let{ AuthUser(it.uid, it.email) }
            ?: throw AuthException(AuthError.UNKNOWN)
        Result.success(user)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(AuthException(e.toAuthError(), e))
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(AuthException(e.toAuthError(), e))
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun authState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            firebaseAuth ->
                val user = firebaseAuth.currentUser?.let { AuthUser(it.uid, it.email) }
                trySend(user)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }
}