package com.dreamteam.breakloop.ui.main.achievements

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.achievement.AchievementRepository
import com.dreamteam.breakloop.domain.AchievementProgress
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.dreamteam.breakloop.data.network.ConnectivityObserver
import com.dreamteam.breakloop.data.network.ConnectivityStatus
import com.dreamteam.breakloop.domain.CatalogSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

/** Used by the Home card and by the achievements grid (each screen gets its own instance). */
class AchievementsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AchievementRepository(application)

    private val _uiState = MutableStateFlow<AchievementsUiState>(AchievementsUiState.Loading)
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    // Celebration currently on screen; the rest wait in the queue
    private val _celebration = MutableStateFlow<AchievementProgress?>(null)
    val celebration: StateFlow<AchievementProgress?> = _celebration.asStateFlow()
    private val celebrationQueue = ArrayDeque<AchievementProgress>()

    private var loadJob: Job? = null

    // Same connectivity source as the Focus tab; each screen shows its own message
    val connectivityStatus: StateFlow<ConnectivityStatus> =
        ConnectivityObserver(application).status
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityStatus.CHECKING)

    init {
        load() // evaluation when the app (Home) or the achievements screen opens

        // Eventual connectivity: when the network comes back, replace the offline catalog with the server's
        viewModelScope.launch {
            connectivityStatus.collect { status ->
                val current = _uiState.value
                if (status == ConnectivityStatus.CONNECTED &&
                    current is AchievementsUiState.Content &&
                    current.source != CatalogSource.NETWORK
                ) {
                    load()
                }
            }
        }
    }

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            _uiState.value = AchievementsUiState.Error
            return
        }

        loadJob?.cancel() // a newer request replaces an older one
        loadJob = viewModelScope.launch {
            if (_uiState.value !is AchievementsUiState.Content) {
                _uiState.value = AchievementsUiState.Loading
            }
            try {
                val catalog = repository.loadCatalog()
                val items = repository.evaluate(uid, catalog.achievements)
                _uiState.value = AchievementsUiState.Content(items, catalog.source)

                celebrationQueue.clear()
                celebrationQueue.addAll(repository.pendingCelebrations(uid, items))
                showNextCelebration()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AchievementsViewModel", "load failed", e)
                _uiState.value = AchievementsUiState.Error
            }
        }
    }

    fun dismissCelebration() {
        val current = _celebration.value ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _celebration.value = null
        viewModelScope.launch {
            repository.markCelebrationSeen(uid, current.achievement.id)
            showNextCelebration()
        }
    }

    private fun showNextCelebration() {
        _celebration.value = celebrationQueue.removeFirstOrNull()
    }
}