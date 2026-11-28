package com.dreamteam.breakloop.background.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.preferences.ProfilePreferences
import com.dreamteam.breakloop.data.remote.ProfileRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

class MyProfileSyncWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        val uid = FirebaseAuth.getInstance().currentUser?.uid?: return Result.success()
        val preferences = ProfilePreferences(applicationContext)

        val remote = ProfileRemoteDataSource(RetrofitInstance.profileApi, FirebaseAuth.getInstance())
        val profile = preferences.observe(uid).first()?: return Result.success()
        if( !profile.pendingUpload) return Result.success()
        try {
            remote.updateMyProfile(profile.name, null)
            preferences.markSynced(uid, profile.updatedAt)
            return Result.success()
        } catch (e: IOException) {
            return Result.retry()
        } catch (e: HttpException) {
            return if (e.code() ==401||e.code()>=500)  Result.retry() else Result.success()
        }
    }
}