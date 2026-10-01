package com.dreamteam.breakloop.ui.account.auth

data class AuthUiState(

    val email : String = "",
    val password : String = "",
    val mode : AuthMode = AuthMode.LOGIN,
    val isLoading : Boolean = false,
    val emailError : String? = null,
    val passwordError : String? = null,
    val error : String? = null



)
