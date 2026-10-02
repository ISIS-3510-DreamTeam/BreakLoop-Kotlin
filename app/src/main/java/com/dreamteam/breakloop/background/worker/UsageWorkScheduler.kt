package com.dreamteam.breakloop.background.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object UsageWorkScheduler {
    private const val WORK_NAME = "usage_aggregation_work"
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<UsageAggregationWorker>( 15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }
}