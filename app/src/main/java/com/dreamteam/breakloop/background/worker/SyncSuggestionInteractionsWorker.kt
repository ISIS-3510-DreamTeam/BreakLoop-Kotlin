package com.dreamteam.breakloop.background.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.remote.SuggestionInteractionRemoteDataSource
import com.dreamteam.breakloop.data.remote.exception.NoSessionException
import com.dreamteam.breakloop.data.repository.SuggestionInteractionRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.google.firebase.auth.FirebaseAuth
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.CancellationException

class SyncSuggestionInteractionsWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = SuggestionInteractionRepositoryImpl(
            db.suggestionInteractionDao(), remote = SuggestionInteractionRemoteDataSource(
                RetrofitInstance.suggestionInteractionApi, FirebaseAuth.getInstance()
            ), syncScheduler = WorkManagerSyncScheduler(applicationContext)
        )
        return try {
            repository.syncUnsynced()
            Result.success()
        } catch (_: NoSessionException) {

            Result.success()
        } catch (e: IOException) {
            Log.w("SyncInteractions", "red: ${e.message}")
            Result.retry()
        } catch (e: HttpException) {
            val code = e.code()
            Log.e("SyncInteractions", "http ${code}: ${e.response()?.errorBody()?.string()}", e)
            if (code == 401 || code == 408 || code == 429 || code in 500..599) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SyncInteractions", "error inesperado", e)
            Result.failure()
        }
    }
}