package com.dreamteam.breakloop.data.local.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.repository.PhoneCheckRepository

class PhonePickupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {
        return try {
            val repository = PhoneCheckRepository(
                context = applicationContext
            )

            repository.syncUnsyncedPickups()

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}