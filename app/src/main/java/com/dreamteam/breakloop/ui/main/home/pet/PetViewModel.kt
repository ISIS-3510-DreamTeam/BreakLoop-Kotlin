package com.dreamteam.breakloop.ui.main.home.pet

import android.app.Application
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.pet.PetRepository
import com.dreamteam.breakloop.data.system.NetworkChecker
import com.dreamteam.breakloop.domain.PetMood
import com.dreamteam.breakloop.domain.usecase.CalculatePetMoodUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class PetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PetRepository(application)
    private val db = AppDatabase.getInstance(application)
    private val calculateMood = CalculatePetMoodUseCase()

    private val _uiState = MutableStateFlow(PetUiState())
    val uiState: StateFlow<PetUiState> = _uiState.asStateFlow()

    init {
        loadCatalog()
        observePet()
        observeXpToday()
        repository.scheduleSync()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            val pets = repository.getCatalog()
            _uiState.update { it.copy(catalog = pets) }
        }
    }

    fun changePet(petId: String) {
        viewModelScope.launch {
            repository.changePet(petId)
            if (!NetworkChecker.isOnline(getApplication())) {
                Toast.makeText(
                    getApplication(),
                    "You're offline. Your pet will change when your connection is back.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Reacts to every change in DataStore (new pet downloaded, mood changed, pending choice).
    private fun observePet() {
        viewModelScope.launch {
            repository.observeState().collect { state ->
                val bitmap = withContext(Dispatchers.IO) { loadImage(state.mood) }
                _uiState.update {
                    it.copy(
                        currentPetId = state.currentPetId,
                        pendingPetId = state.pendingPetId,
                        mood = state.mood,
                        image = bitmap
                    )
                }
            }
        }
    }

    // Adds today's XP from offline activities + focus sessions and updates the mood.
    private fun observeXpToday() {
        val startOfDay = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        viewModelScope.launch {
            combine(
                db.activityLogDao().observeXpSince(startOfDay),
                db.focusSessionDao().observeXpSince(startOfDay)
            ) { activityXp, focusXp -> activityXp + focusXp }
                .collect { xpToday ->
                    _uiState.update { it.copy(xpToday = xpToday) }
                    repository.updateMood(calculateMood.calculate(xpToday))
                }
        }
    }

    private fun loadImage(mood: PetMood): ImageBitmap? {
        val file = repository.getImageFile(mood) ?: return null
        return BitmapFactory.decodeFile(file.path)?.asImageBitmap()
    }
}