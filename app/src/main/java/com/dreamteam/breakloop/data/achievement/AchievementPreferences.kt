package com.dreamteam.breakloop.data.achievement

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.achievementDataStore by preferencesDataStore(name = "achievement_prefs")

/** Celebrations the user already saw, per account, so an unlock is celebrated only once. */
class AchievementPreferences(context: Context) {

    private val dataStore = context.applicationContext.achievementDataStore

    private fun seenKey(uid: String) = stringSetPreferencesKey("seen_celebrations_$uid")

    suspend fun getSeenCelebrations(uid: String): Set<String> =
        dataStore.data.first()[seenKey(uid)] ?: emptySet()

    suspend fun markCelebrationSeen(uid: String, achievementId: String) {
        dataStore.edit { prefs ->
            val seen = prefs[seenKey(uid)] ?: emptySet()
            prefs[seenKey(uid)] = seen + achievementId
        }
    }
}