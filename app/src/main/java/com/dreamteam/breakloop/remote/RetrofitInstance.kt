package com.dreamteam.breakloop.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val focusApi: FocusApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.0.6:8080/")
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(FocusApi::class.java)
    }
}