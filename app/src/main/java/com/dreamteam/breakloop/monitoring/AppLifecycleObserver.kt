
package com.dreamteam.breakloop.monitoring

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

class AppLifecycleObserver(
    private val onAppForeground: (Long) -> Unit,
    private val onAppBackground: () -> Unit
) : DefaultLifecycleObserver {

    private var backgroundTime: Long? = null

    fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun stop() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        val tiempoEnSegundoPlano = backgroundTime?.let {
            System.currentTimeMillis() - it
        } ?: 0L

        backgroundTime = null
        onAppForeground(tiempoEnSegundoPlano)
    }

    override fun onStop(owner: LifecycleOwner) {
        backgroundTime = System.currentTimeMillis()
        onAppBackground()
    }
}
