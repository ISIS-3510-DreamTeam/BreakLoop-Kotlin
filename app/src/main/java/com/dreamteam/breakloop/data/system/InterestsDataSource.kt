package com.dreamteam.breakloop.data.system

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class InterestsDataSource(
    private val context: Context
) {
    private val INTERESTS_KEY = stringSetPreferencesKey("interests")

    suspend fun getInterests(): Set<ActivityCategory> {
        val savedStrings = context.dataStore.data.first()[INTERESTS_KEY] ?: emptySet()
        return savedStrings.mapNotNull { name ->
            runCatching { ActivityCategory.valueOf(name) }.getOrNull()
        }.toSet()
    }

    suspend fun saveInterests(interests: Set<ActivityCategory>) {
        context.dataStore.edit { preferences ->
            preferences[INTERESTS_KEY] = interests.map { it.name }.toSet()
        }
    }
}