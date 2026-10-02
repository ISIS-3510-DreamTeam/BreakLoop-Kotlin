package com.dreamteam.breakloop.ui.account.auth

data class AuthUiState(

    val email : String = "",
    val password : String = "",
    val mode : AuthMode = AuthMode.LOGIN,
    val isLoading : Boolean = false,
    val emailError : String? = null,
    val passwordError : String? = null,
    val error : String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val resetEmailSent: Boolean = false



)
