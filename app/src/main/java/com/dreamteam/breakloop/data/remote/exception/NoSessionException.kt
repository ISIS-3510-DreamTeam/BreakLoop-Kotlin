package com.dreamteam.breakloop.data.remote.exception

class NoSessionException(
    message: String = "No authenticated user"
) : Exception(message)