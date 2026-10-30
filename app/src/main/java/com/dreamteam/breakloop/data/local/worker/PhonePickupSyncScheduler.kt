package com.dreamteam.breakloop.data.local.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class PhonePickupSyncScheduler(
    context: Context
) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest =
            OneTimeWorkRequestBuilder<PhonePickupWorker>()
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniqueWork(
            "phone-pickup-sync",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}