
package com.dreamteam.breakloop.monitoring

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AppLifecycleEvents {

    private val _appReturned = MutableSharedFlow<Long>(
        replay = 1,
        extraBufferCapacity = 1
    )

    val appReturned = _appReturned.asSharedFlow()

    fun notifyAppReturned(timeInBackground: Long) {
        _appReturned.tryEmit(timeInBackground)
    }
}
