package com.dreamteam.breakloop.domain.repository

import android.net.Uri
import com.dreamteam.breakloop.domain.MyProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun observeMyProfile( uid: String): Flow<MyProfile?>

    suspend fun updateName(uid: String, name: String)

    suspend fun updatePhoto(uid: String, uri: Uri): Boolean

    suspend fun clear(uid: String)
}