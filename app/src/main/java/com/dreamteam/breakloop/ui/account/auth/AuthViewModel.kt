package com.dreamteam.breakloop.ui.account.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.auth.AuthError
import com.dreamteam.breakloop.data.auth.AuthException
import com.dreamteam.breakloop.data.auth.AuthRepository
import com.dreamteam.breakloop.data.auth.AuthUser
import com.dreamteam.breakloop.data.auth.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//View model for Auth
class AuthViewModel(
    private val repository: AuthRepository = FirebaseAuthRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value.trim(), emailError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun toggleMode() {
        val mode : AuthMode = uiState.value.mode
        var aMode : AuthMode = AuthMode.LOGIN
        if (mode == AuthMode.LOGIN) {
            aMode = AuthMode.SIGNUP
        }
        if (mode == AuthMode.SIGNUP) {
            aMode = AuthMode.LOGIN
        }
        _uiState.update { it.copy(mode = aMode, emailError = null, passwordError = null, confirmPasswordError = null, error = null) }
    }

    fun showRecover() {
        _uiState.update { it.copy(mode = AuthMode.RECOVER, emailError = null, passwordError = null, confirmPasswordError = null, error = null) }
    }

    fun showLogin() {
        _uiState.update { it.copy(mode = AuthMode.LOGIN, emailError = null, passwordError = null, confirmPasswordError = null, error = null, resetEmailSent = false) }
    }

    private fun validate(): Boolean {
        if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.value.email).matches())) {
            _uiState.update { it.copy(emailError = "This email is not valid") }
            return false
        }
        if (!( uiState.value.password.length >= 8)) {
            _uiState.update { it.copy(passwordError = "This password is not strong enough") }
            return false
        }
        val isSignUp : Boolean = (uiState.value.mode == AuthMode.SIGNUP)
        if (isSignUp) {
            if (! (uiState.value.password == uiState.value.confirmPassword)){
                _uiState.update { it.copy(confirmPasswordError = "Passwords are not identical") }
                return false
            }
        }
        return true

    }

    fun submit() {
        if (uiState.value.mode == AuthMode.RECOVER) {
            sendPasswordReset()
            return
        }
        if (! validate()) { return }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            var result : Result<AuthUser>? = null
            if (uiState.value.mode == AuthMode.LOGIN) {
                result = repository.signIn(uiState.value.email,uiState.value.password)
            } else {
                result = repository.signUp(uiState.value.email,uiState.value.password)
            }
            val error = (result.exceptionOrNull() as? AuthException)?.error //casts to authException
            var errorMsg : String? = null
            if (error != null) {
                errorMsg = error.ToMessage()
            }
                    _uiState.update { it.copy(isLoading = false, error = errorMsg) }
        }
    }

    fun sendPasswordReset() {
        if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.value.email).matches())) {
            _uiState.update { it.copy(emailError = "This email is not valid") }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            var result : Result<Unit>? = null
            result = repository.sendPasswordReset(uiState.value.email)
            val error = (result.exceptionOrNull() as? AuthException)?.error //casts to authException
            var errorMsg : String? = null
            if (error != null) {
                errorMsg = error.ToMessage()
            } else {
                _uiState.update { it.copy(isLoading = false, resetEmailSent = true) }
            }
            _uiState.update { it.copy(isLoading = false, error = errorMsg) }
        }

    }

    private fun AuthError.ToMessage(): String {//WEAK_PASSWORD, NETWORK, TOO_MANY_REQUESTS, UNKNOWN, EMAIL_IN_USE, INVALID_CREDENTIALS
        var msg: String = ""
        when (this) {
            AuthError.WEAK_PASSWORD -> msg = "This password isn't strong enough"
            AuthError.NETWORK -> msg = "Check your connection"
            AuthError.TOO_MANY_REQUESTS -> msg = "The system is overloaded, try again later"
            AuthError.EMAIL_IN_USE -> msg = "This emails is already linked to an account"
            AuthError.INVALID_CREDENTIALS -> msg = "Email or password don't match"
            else -> msg = "An error occurred, try again later"
        }
        return msg
    }
}

