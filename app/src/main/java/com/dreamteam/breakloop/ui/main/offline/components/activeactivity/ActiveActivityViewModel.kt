package com.dreamteam.breakloop.ui.main.offline.components.activeactivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.remote.ActivityLogApi
import com.dreamteam.breakloop.data.remote.ActivityLogRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.repository.ActivityLogRepositoryImpl
import com.dreamteam.breakloop.data.repository.ActivityRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.dreamteam.breakloop.data.system.ActivityCatalogDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ActiveActivityViewModel(
    application: Application
): AndroidViewModel(application){

    private val db = AppDatabase.getInstance(application.applicationContext)


    private val activityLogRepository = ActivityLogRepositoryImpl(
        db.activityLogDao(),
        remote = ActivityLogRemoteDataSource(RetrofitInstance.activityLogApi, FirebaseAuth.getInstance()),
        syncScheduler = WorkManagerSyncScheduler(application.applicationContext)
    )

    private val activityRepository =
        ActivityRepositoryImpl(ActivityCatalogDataSource(application.applicationContext))


    val openlog =activityLogRepository.observeOpenLog()
        .map { log ->
            if (log == null) {
                null
            } else {
                Pair(log, activityRepository.getActivity(log.activityId))
            }
        }
    val tick = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }

    val uiState: StateFlow<ActiveActivityUiState> = combine(
        openlog,
        tick
    ) { open, tick ->
        if (open == null || open.second == null){
            return@combine ActiveActivityUiState.Hidden
        }
        val endedAt = open.first.startedAt + open.second!!.durationMin *60000
        val remaining = endedAt - tick
        if (remaining <= 0){
            return@combine ActiveActivityUiState.Finished(open.second!!.id, open.second!!.title)
        } else {
            return@combine ActiveActivityUiState.Running(open.second!!.id, open.second!!.title, (remaining/1000).toInt())
        }
    }.stateIn( viewModelScope, SharingStarted.WhileSubscribed(5000), ActiveActivityUiState.Hidden)


}
