package com.dreamteam.breakloop.background.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.remote.ProfileRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import okio.IOException
import retrofit2.HttpException

class ProfileViewSyncWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (FirebaseAuth.getInstance().currentUser==null) {
            return Result.success()
        }

        val dao = AppDatabase.getInstance(applicationContext).pendingProfileViewDao()
        val remote = ProfileRemoteDataSource(RetrofitInstance.profileApi, FirebaseAuth.getInstance())

        for (visit in dao.getAll()) {
            try {
                remote.logProfileView(visit.viewedAt)
                dao.delete(visit.viewedAt)
            } catch (e: IOException) {
                return Result.retry()
            } catch (e: HttpException) {
                if (e.code() ==401 || e.code()>=500) return Result.retry()
                dao.delete(visit.viewedAt)
            }
        }
        return Result.success()
    }

}