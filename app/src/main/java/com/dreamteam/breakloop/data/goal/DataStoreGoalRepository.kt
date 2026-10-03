package com.dreamteam.breakloop.data.goal

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dreamteam.breakloop.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Creates DataStore file
private val Context.goalDataStore by preferencesDataStore(name = "goal_prefs")

    class DataStoreGoalRepository(context: Context) : GoalRepository {

        private val dataStore = context.applicationContext.goalDataStore

        // key
        private val goalKey = longPreferencesKey("daily_goal_ms")
        private val lastNotifKey = stringPreferencesKey("last_notified_date")

        override fun observeDailyGoalMs(): Flow<Long?> {
            return dataStore.data.map{ prefs -> prefs[goalKey] }
        }

        override suspend fun setDailyGoalMs(goalMs: Long) {
            dataStore.edit { prefs -> prefs[goalKey] = goalMs
                prefs.remove(lastNotifKey) }
        }

        override suspend fun getLastNotifiedDate(): String? {
            return dataStore.data.first()[lastNotifKey]
        }

        override suspend fun setLastNotifiedDate(date: String) {
            dataStore.edit { prefs -> prefs[lastNotifKey] = date }
        }

    }