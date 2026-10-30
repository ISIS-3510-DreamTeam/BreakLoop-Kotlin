package com.dreamteam.breakloop.data.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkChecker {

    //revisa si hay conexion y si da internet

    fun isOnline(context: Context): Boolean {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}