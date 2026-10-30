package com.dreamteam.breakloop.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://breakloop-backend-14746672383.southamerica-west1.run.app/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(
                "application/json".toMediaType()
            ))
            .build()
    }

    val focusApi: FocusApi by lazy {
        retrofit
            .create(FocusApi::class.java)
    }
    val activityLogApi: ActivityLogApi by lazy {
        retrofit
            .create(ActivityLogApi::class.java)
    }

    val usageApi: UsageApi by lazy {
        retrofit
            .create(UsageApi::class.java)
    }
}