package com.dreamteam.breakloop.data.local.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.local.repository.FocusSessionRepository
import com.dreamteam.breakloop.remote.RetrofitInstance

class FocusSessionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {
        return try {

            val database = AppDatabase.getInstance(
                applicationContext
            )

            val repository = FocusSessionRepository(
                focusSessionDao = database.focusSessionDao(),
                focusApi = RetrofitInstance.focusApi
            )

            repository.syncUnsyncedSessions()

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}