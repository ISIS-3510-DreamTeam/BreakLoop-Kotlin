package com.dreamteam.breakloop.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {

    private const val BASE_URL =
        "https://breakloop-backend-14746672383.southamerica-west1.run.app/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val focusApi: FocusApi by lazy { retrofit.create(FocusApi::class.java) }

    val petApi: PetApi by lazy { retrofit.create(PetApi::class.java) }
}