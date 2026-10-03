package com.dreamteam.breakloop.data.system

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.dreamteam.breakloop.domain.Coordinates
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class LocationDataSource(
    private val context: Context,
    private val permissionsDataSource: PermissionsDataSource
) {
    @SuppressLint("MissingPermission")
    suspend fun getApproximateLocation(): Coordinates?{
        Log.d("LocationTest", "Permiso: ${permissionsDataSource.hasLocationAccess()}")
        if (!permissionsDataSource.hasLocationAccess()){
            return null
        }
        try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val location =  withTimeoutOrNull(2000L.milliseconds) {
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .await() ?: client.lastLocation.await()
            }
            if (location == null){
                return null
            } else {
                return Coordinates(
                    location.latitude,
                    location.longitude
                )
            }
        } catch (error : Exception) {
            Log.e("LocationTest", "Error obteniendo ubicación", error)
            return null
        }
    }
}