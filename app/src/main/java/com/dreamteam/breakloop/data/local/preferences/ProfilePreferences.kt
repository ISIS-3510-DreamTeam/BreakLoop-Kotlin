package com.dreamteam.breakloop.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dreamteam.breakloop.data.system.dataStore
import com.dreamteam.breakloop.domain.MyProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ProfilePreferences(
    private val context: Context
) {

    private fun profileNameKey(uid: String) = stringPreferencesKey("profile_name_$uid")
    private fun profileLocalPhotoKey(uid: String) = stringPreferencesKey("profile_local_photo_$uid")
    private fun profileRemotePhotoKey(uid: String) = stringPreferencesKey("profile_remote_photo_$uid")
    private fun profilePendingKey(uid:String) = booleanPreferencesKey("profile_pending_$uid")
    private fun profileUpdatedAtKey(uid: String) = longPreferencesKey("profile_updated_at_$uid")

    fun observe (
        uid: String
    ): Flow<MyProfile?> {
        return context.dataStore.data.map {
            preferences ->
            val name = preferences[profileNameKey(uid)]
            val localPhoto = preferences[profileLocalPhotoKey(uid)]
            if (name== null && localPhoto == null) {
                null
            }
            else {
                MyProfile(
                    uid,
                    name?: "",
                    localPhoto,
                    preferences[profileRemotePhotoKey(uid)],
                    preferences[profilePendingKey(uid)]?: false,
                    preferences[profileUpdatedAtKey(uid)]?:0L
                )
            }
        }
    }

    suspend fun saveName(
        uid: String,
        name: String
    ) {
        context.dataStore.edit {
            it[profileNameKey(uid)] = name
            it[profilePendingKey(uid)] = true
            it[profileUpdatedAtKey(uid)] = System.currentTimeMillis()
        }
    }

    suspend fun saveLocalPhoto(
        uid: String,
        path: String
    ) {
        context.dataStore.edit {
            it[profileLocalPhotoKey(uid)] = path
            it[profilePendingKey(uid)] = true
            it[profileUpdatedAtKey(uid)] = System.currentTimeMillis()
        }
    }
    suspend fun clearKeys(
        uid: String
    ) {
        context.dataStore.edit {
            it.remove(profileLocalPhotoKey(uid))
            it.remove(profileNameKey(uid))
            it.remove(profileRemotePhotoKey(uid))
            it.remove(profilePendingKey(uid))
            it.remove(profileUpdatedAtKey(uid))
        }
    }

}