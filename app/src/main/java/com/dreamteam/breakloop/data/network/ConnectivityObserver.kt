package com.dreamteam.breakloop.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class ConnectivityStatus {
    CHECKING,
    CONNECTED,
    NO_INTERNET,
    DISCONNECTED
}

class ConnectivityObserver(
    context: Context
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val status: Flow<ConnectivityStatus> = callbackFlow {

        fun obtenerEstado(): ConnectivityStatus {
            val network = connectivityManager.activeNetwork
                ?: return ConnectivityStatus.DISCONNECTED

            val capabilities =
                connectivityManager.getNetworkCapabilities(network)
                    ?: return ConnectivityStatus.DISCONNECTED

            val tieneInternet =
                capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )

            val internetValidado =
                capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )

            return when {
                !tieneInternet -> ConnectivityStatus.NO_INTERNET
                !internetValidado -> ConnectivityStatus.NO_INTERNET
                else -> ConnectivityStatus.CONNECTED
            }
        }

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                trySend(obtenerEstado())
            }

            override fun onLost(network: Network) {
                trySend(obtenerEstado())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                trySend(obtenerEstado())
            }
        }

        trySend(obtenerEstado())

        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}