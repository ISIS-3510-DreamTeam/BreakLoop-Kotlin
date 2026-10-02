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
            .baseUrl(
                "https://breakloop-backend-14746672383.southamerica-west1.run.app/"
            )
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(FocusApi::class.java)
    }
}