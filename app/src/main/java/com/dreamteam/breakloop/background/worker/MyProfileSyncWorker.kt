package com.dreamteam.breakloop.background.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dreamteam.breakloop.data.local.preferences.ProfilePreferences
import com.dreamteam.breakloop.data.remote.ProfilePhotoRemoteDataSource
import com.dreamteam.breakloop.data.remote.ProfileRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class MyProfileSyncWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        val uid = FirebaseAuth.getInstance().currentUser?.uid?: return Result.success()
        val preferences = ProfilePreferences(applicationContext)

        val remote = ProfileRemoteDataSource(RetrofitInstance.profileApi, FirebaseAuth.getInstance())
        val photoRemote = ProfilePhotoRemoteDataSource()
        val profile = preferences.observe(uid).first()?: return Result.success()
        if( !profile.pendingUpload) return Result.success()
        try {
            val photoUrl = profile.localPhotoPath?.let { path ->
                // para que no se me duplique en la subida la imagen , mejor poner solo este
                val version = File(path).nameWithoutExtension.substringAfter('_').toLongOrNull()
                    ?: profile.updatedAt
                photoRemote.upload(uid, path, version)
            }
            remote.updateMyProfile(profile.name, photoUrl)
            preferences.markSynced(uid, profile.updatedAt)
            return Result.success()
        } catch (e: IOException) {
            return Result.retry()
        } catch (e: StorageException) {
            val transient = e.errorCode == StorageException.ERROR_RETRY_LIMIT_EXCEEDED ||
                e.errorCode == StorageException.ERROR_NOT_AUTHENTICATED ||
                e.httpResultCode >= 500
            return if (transient) Result.retry() else Result.success()
        } catch (e: HttpException) {
            return if (e.code() ==401||e.code()>=500)  Result.retry() else Result.success()
        }
    }
}