package com.dreamteam.breakloop.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class ProfilePhotoRemoteDataSource(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    suspend fun upload(uid: String, localPath: String, version: Long): String {
        val ref = storage.reference.child("profile_photos/${uid}_$version.jpg")
        ref.putFile(Uri.fromFile(File(localPath))).await()
        return ref.downloadUrl.await().toString()
    }
}
