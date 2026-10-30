package com.dreamteam.breakloop.data.system

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import kotlinx.coroutines.flow.first

class PlacePreferenceDataSource(
    private val context: Context
) {

    private val PLACE_KEY = stringPreferencesKey("place")
    suspend fun getPlace(): PlaceLabel? {
        val saved = context.dataStore.data.first()[PLACE_KEY] ?: return null
        return runCatching {
            PlaceLabel.valueOf(saved)
        }.getOrNull()
    }

    suspend fun setPlace(place: PlaceLabel?) {
        context.dataStore.edit {
            preferences ->
            if ( place == null ){
                preferences.remove( PLACE_KEY)
            } else {
                preferences[PLACE_KEY] = place.name
            }
        }
    }
}