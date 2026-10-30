package com.dreamteam.breakloop.data.system

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.dreamteam.breakloop.data.remote.dto.FriendDto
import com.dreamteam.breakloop.data.remote.dto.UserProfileDto
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FriendsLocalDataSource(
    private val context: Context
) {
    private fun profileKey(uid: String) = stringPreferencesKey("friends_profile_$uid")
    private fun friendsKey(uid: String) = stringPreferencesKey("friends_list_$uid")
    private fun lastUpdatedKey(uid: String) = stringPreferencesKey("friends_last_updated_$uid")
    private fun pendingKey(uid: String) = stringSetPreferencesKey("friends_pending_codes_$uid")

    suspend fun getProfile(uid: String): UserProfileDto? {
        val text = context.dataStore.data.first()[profileKey(uid)] ?: return null
        return try {
            Json.decodeFromString<UserProfileDto>(text)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveProfile(
        uid: String,
        profile: UserProfileDto
    ) {
        context.dataStore.edit {
            it[profileKey(uid)] = Json.encodeToString(profile)
        }
    }

    suspend fun getFriends(uid: String): List<FriendDto>? {
        val text = context.dataStore.data.first()[friendsKey(uid)] ?: return null
        return try {
            Json.decodeFromString<List<FriendDto>>(text)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getLastUpdated(uid: String): Long? {
        return context.dataStore.data.first()[lastUpdatedKey(uid)]?.toLongOrNull()
    }

    suspend fun saveFriends(
        uid: String,
        friends: List<FriendDto>,
        lastUpdated: Long
    ) {
        context.dataStore.edit {
            it[friendsKey(uid)] = Json.encodeToString(friends)
            it[lastUpdatedKey(uid)] = lastUpdated.toString()
        }
    }

    suspend fun getPendingCodes(
        uid: String,
        codes: List<String> = emptyList()
    ): Set<String> {
        val saved = context.dataStore.data.first()[pendingKey(uid)] ?: emptySet()
        return saved
    }

    suspend fun savePendingCodes(
        uid: String,
        codes: Set<String>
    ) {
        context.dataStore.edit {
            it[pendingKey(uid)] = codes
        }
    }
}