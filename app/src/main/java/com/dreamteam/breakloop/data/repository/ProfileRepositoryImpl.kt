package com.dreamteam.breakloop.data.repository

import android.net.Uri
import com.dreamteam.breakloop.data.local.files.ProfilePhotoDataSource
import com.dreamteam.breakloop.data.local.preferences.ProfilePreferences
import com.dreamteam.breakloop.domain.MyProfile
import com.dreamteam.breakloop.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val preferences: ProfilePreferences,
    private val photos: ProfilePhotoDataSource,
) : ProfileRepository {
    override fun observeMyProfile(uid: String): Flow<MyProfile?> {
        return preferences.observe(uid)
    }

    override suspend fun updateName(uid: String, name: String) {
        preferences.saveName(uid, name)

    }

    override suspend fun updatePhoto(uid: String, uri: Uri): Boolean {
        val path = photos.save(uri) ?: return false
        preferences.saveLocalPhoto(uid, path)
        return true
    }

    override suspend fun clear(uid: String) {

            preferences.clearKeys(uid)
            photos.deleteAll()
    }

}