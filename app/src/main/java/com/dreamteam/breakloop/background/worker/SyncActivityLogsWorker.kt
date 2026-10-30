package com.dreamteam.breakloop.background.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.remote.ActivityLogRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.remote.exception.NoSessionException
import com.dreamteam.breakloop.data.repository.ActivityLogRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.google.firebase.auth.FirebaseAuth
import retrofit2.HttpException
import java.io.IOException

class SyncActivityLogsWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = ActivityLogRepositoryImpl(
            db.activityLogDao(),
            remote = ActivityLogRemoteDataSource(RetrofitInstance.activityLogApi, FirebaseAuth.getInstance()),
            syncScheduler = WorkManagerSyncScheduler(applicationContext)
        )
        Log.w("SyncActivityLogs", "Intentando sincronizar")
        return try {
            repository.syncActivityLogs()
            Result.success()
        } catch (_: NoSessionException) {

            Result.success()
        } catch (_: IOException) {
            Log.w("SyncActivityLogs", "Error de red al sincronizar IOException")
            Result.retry()
        } catch (e: HttpException) {

            val code = e.code()
            if (code == 401 || code == 408 || code == 429 || code in 500..599) {
                Result.retry()
            } else {
                Log.e("SyncActivityLogsWorker", "rechazado: ${e.code()}", e)
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("SyncActivityLogsWorker", "Error inesperado al sincronizar", e)
            Result.failure()
        }
    }

}