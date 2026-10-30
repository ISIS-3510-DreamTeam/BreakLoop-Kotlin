package com.dreamteam.breakloop.background.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.remote.UsageRemoteDataSource
import com.dreamteam.breakloop.data.remote.exception.NoSessionException
import com.dreamteam.breakloop.data.repository.FlaggedAppsRepositoryImpl
import com.dreamteam.breakloop.data.repository.UsageRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateBaselineUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateFlaggedTimeUseCase
import com.google.firebase.auth.FirebaseAuth
import retrofit2.HttpException
import java.io.IOException

class SyncDailyUsageWorker (
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = UsageRepositoryImpl(
            UsageEventsDataSource(
                applicationContext,
                PermissionsDataSource(applicationContext)
            ),
            InstalledAppsDataSource(applicationContext),
            remote = UsageRemoteDataSource(RetrofitInstance.usageApi, FirebaseAuth.getInstance()),
            syncScheduler = WorkManagerSyncScheduler(applicationContext),
            aggregateUsageEventsUseCase = AggregateUsageEventsUseCase(),
            appUsageDao = db.appUsageDao(),
            dailyUsageStatsDao = db.dailyUsageStatsDao(),
            calculateBaselineUseCase = CalculateBaselineUseCase(),
            calculateFlaggedTimeUseCase = CalculateFlaggedTimeUseCase(),
            flaggedAppsRepositoryImpl = FlaggedAppsRepositoryImpl(db.flaggedAppDao())
        )
        return try {
            repository.syncUnsyncedDays()
            Result.success()
        } catch (_: NoSessionException) {

            Result.success()
        } catch (_: IOException) {
            Log.w("SyncDailyUsageWorker", "Error de red al sincronizar IOException")
            Result.retry()
        } catch(e: HttpException) {
            Result.retry()
        } catch (e: Exception) {
                Result.failure()

        }
    }
}