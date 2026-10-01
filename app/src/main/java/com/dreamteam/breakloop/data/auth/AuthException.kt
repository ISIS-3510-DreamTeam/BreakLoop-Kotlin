package com.dreamteam.breakloop.data.auth

class AuthException(val error: AuthError, cause: Throwable? = null) : Exception(error.name)