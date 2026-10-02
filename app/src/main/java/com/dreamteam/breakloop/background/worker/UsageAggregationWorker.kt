package com.dreamteam.breakloop.background.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.local.repository.UsageRepositoryImpl
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import java.time.LocalDate
import java.time.ZoneId

class UsageAggregationWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val repositoryImpl = UsageRepositoryImpl(
                usageEventsDataSource = UsageEventsDataSource(this.applicationContext,
                    PermissionsDataSource(this.applicationContext)
                ),
                installedAppsDataSource = InstalledAppsDataSource(this.applicationContext),
                aggregateUsageEventsUseCase = AggregateUsageEventsUseCase(),
                appUsageDao =db.appUsageDao(),
                dailyUsageStatsDao =db.dailyUsageStatsDao()
            )
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val yesterdayStats = db.dailyUsageStatsDao().getDailyUsageStatsOnce(yesterday.toString())


            if(yesterdayStats == null || yesterdayStats.updatedAt < startOfToday) {
                repositoryImpl.refreshDay(yesterday.toString())
            }
            repositoryImpl.refreshDay(today.toString())
            Log.d("UsageAggregationWorker", "Usage aggregation completed successfully")
            Result.success()
        }
        catch (e: Exception) {
            Log.e("UsageAggregationWorker", "Error during usage aggregation", e)
            Result.retry()
        }

    }

}