package com.dreamteam.breakloop.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dreamteam.breakloop.background.worker.SyncActivityLogsWorker
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.sync.SyncScheduler

class WorkManagerSyncScheduler(
    private val context: Context
) : SyncScheduler {
    override fun enqueueActivityLogSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncActivityLogsWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork("sync_activity_logs",
            ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }

    override fun enqueueSuggestionInteractionSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncActivityLogsWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork("sync_suggestion_interactions",
            ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }
}