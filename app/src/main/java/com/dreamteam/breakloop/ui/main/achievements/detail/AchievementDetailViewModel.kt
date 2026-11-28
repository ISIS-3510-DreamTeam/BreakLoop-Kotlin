package com.dreamteam.breakloop.ui.main.achievements.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dreamteam.breakloop.data.achievement.AchievementRepository
import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.ui.navigation.AchievementDetail
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.dreamteam.breakloop.data.network.ConnectivityObserver
import com.dreamteam.breakloop.data.network.ConnectivityStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

sealed interface AchievementDetailUiState {
    data object Loading : AchievementDetailUiState
    data object NotFound : AchievementDetailUiState
    data class Content(val progress: AchievementProgress) : AchievementDetailUiState
}

class AchievementDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val achievementId = savedStateHandle.toRoute<AchievementDetail>().achievementId
    private val repository = AchievementRepository(application)

    private val _uiState = MutableStateFlow<AchievementDetailUiState>(AchievementDetailUiState.Loading)
    val uiState: StateFlow<AchievementDetailUiState> = _uiState.asStateFlow()
    val connectivityStatus: StateFlow<ConnectivityStatus> =
        ConnectivityObserver(application).status
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityStatus.CHECKING)
    init {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val progress = try {
                // LruCache first: the grid already calculated it, so no new evaluation
                if (uid == null) null else repository.getProgress(uid, achievementId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AchievementDetailVM", "load failed", e)
                null
            }
            _uiState.value =
                if (progress == null) AchievementDetailUiState.NotFound
                else AchievementDetailUiState.Content(progress)
        }
    }
}