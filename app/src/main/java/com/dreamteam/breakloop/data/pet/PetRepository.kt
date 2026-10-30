package com.dreamteam.breakloop.data.pet

import android.content.Context
import com.dreamteam.breakloop.domain.PetMood
import com.dreamteam.breakloop.remote.PetApi
import com.dreamteam.breakloop.remote.PetDto
import com.dreamteam.breakloop.remote.PetStateRequest
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PetRepository(
    private val context: Context,
    private val localStore: PetLocalStore = PetLocalStore(context),
    private val imageStorage: PetImageStorage = PetImageStorage(context),
    private val api: PetApi = RetrofitInstance.petApi
) {

    private val json = Json { ignoreUnknownKeys = true }

    //lo que le pasa al vm
    fun observeState(): Flow<LocalPetState> = localStore.state

    fun getImageFile(mood: PetMood): File? = imageStorage.getImageFile(mood)

    // Saves the choice and lets the worker apply it as soon as there is internet.
    suspend fun changePet(petId: String) {
        localStore.setPendingPet(petId)
        PetSyncWorker.schedule(context)
    }

    suspend fun updateMood(mood: PetMood) {
        if (localStore.setMood(mood)) {
            PetSyncWorker.schedule(context)
        }
    }

    // Resync anything unsynchronized.
    fun scheduleSync() {
        PetSyncWorker.schedule(context)
    }

    // Catalog from server if possible, otherwise the last saved copy.
    suspend fun getCatalog(): List<PetDto> {
        return try {
            val pets = api.getPets()
            localStore.saveCatalog(json.encodeToString(pets))
            pets
        } catch (e: Exception) {
            val saved = localStore.getCatalog() ?: return emptyList()
            json.decodeFromString<List<PetDto>>(saved)
        }
    }

    // Only called by PetSyncWorker, which only runs when there is internet.
    suspend fun syncWithServer() {
        // 1. Apply the pending pet change
        val pendingId = localStore.getState().pendingPetId
        if (pendingId != null) {
            downloadPet(pendingId)
            localStore.finishPetChange(pendingId)
        }

        // 2. Send the current pet and mood to the backend (if out of date)
        val state = localStore.getState()
        val petId = state.currentPetId ?: return
        if (state.synced) return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val response = api.updatePetState(
            uid,
            PetStateRequest(petId, state.mood.name, state.updatedAt)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Could not sync pet. HTTP ${response.code()}")
        }
        localStore.markSynced(state.updatedAt)
    }

    private suspend fun downloadPet(petId: String) {
        val pet = getCatalog().first { it.id == petId }

        // Network and file writing happen on thread pool
        withContext(Dispatchers.IO) {
            imageStorage.startDownload()
            for ((moodName, path) in pet.images) {
                val bytes = api.downloadFile(path).bytes()
                imageStorage.saveDownloadedImage(moodName, bytes)
            }
            imageStorage.finishDownload()
        }
    }
}