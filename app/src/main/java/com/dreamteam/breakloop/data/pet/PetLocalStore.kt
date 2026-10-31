package com.dreamteam.breakloop.data.pet

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dreamteam.breakloop.domain.PetMood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

//Guarda localmente

//datastore file (preferences)
private val Context.petDataStore by preferencesDataStore(name = "pet_prefs")

data class LocalPetState(
    val currentPetId: String?,  // pet whose images are saved on the phone
    val pendingPetId: String?,  // pet the user picked that is not downloaded yet
    val mood: PetMood,
    val synced: Boolean,        // false = the server does not know the latest pet/mood yet
    val updatedAt: Long         // when the pet or mood last changed
)

class PetLocalStore(context: Context) {

    //pet preferences datastore
    private val dataStore = context.applicationContext.petDataStore

    private val currentPetKey = stringPreferencesKey("current_pet_id")
    private val pendingPetKey = stringPreferencesKey("pending_pet_id")
    private val moodKey = stringPreferencesKey("mood")
    private val syncedKey = booleanPreferencesKey("synced")
    private val updatedAtKey = longPreferencesKey("updated_at")
    private val catalogKey = stringPreferencesKey("catalog_json")

    val state: Flow<LocalPetState> = dataStore.data.map { prefs ->
        LocalPetState(
            currentPetId = prefs[currentPetKey],
            pendingPetId = prefs[pendingPetKey],
            mood = PetMood.valueOf(prefs[moodKey] ?: PetMood.SAD.name),
            synced = prefs[syncedKey] ?: true,
            updatedAt = prefs[updatedAtKey] ?: 0L
        )
    }

    suspend fun getState(): LocalPetState = state.first()

    // Overwrites any previous pending choice (solo ultimo)
    suspend fun setPendingPet(petId: String) {
        dataStore.edit { prefs ->
            if (prefs[currentPetKey] == petId) {
                prefs.remove(pendingPetKey) // user went back to the pet they already have
            } else {
                prefs[pendingPetKey] = petId
            }
        }
    }

    // Called after the new pet's images are on disk.
    suspend fun finishPetChange(petId: String) {
        dataStore.edit { prefs ->
            prefs[currentPetKey] = petId
            // If the user picked another pet during the download, keep pending.
            if (prefs[pendingPetKey] == petId) prefs.remove(pendingPetKey)
            prefs[syncedKey] = false
            prefs[updatedAtKey] = System.currentTimeMillis()
        }
    }

    // Returns true only if the mood really changed.
    suspend fun setMood(mood: PetMood): Boolean {
        var changed = false
        dataStore.edit { prefs ->
            if (prefs[moodKey] != mood.name) {
                prefs[moodKey] = mood.name
                prefs[syncedKey] = false
                prefs[updatedAtKey] = System.currentTimeMillis()
                changed = true
            }
        }
        return changed
    }

    // Only mark as synced if nothing changed while we were uploading.
    suspend fun markSynced(sentUpdatedAt: Long) {
        dataStore.edit { prefs ->
            if ((prefs[updatedAtKey] ?: 0L) == sentUpdatedAt) prefs[syncedKey] = true
        }
    }

    suspend fun saveCatalog(json: String) {
        dataStore.edit { prefs -> prefs[catalogKey] = json }
    }

    suspend fun getCatalog(): String? = dataStore.data.first()[catalogKey]
}