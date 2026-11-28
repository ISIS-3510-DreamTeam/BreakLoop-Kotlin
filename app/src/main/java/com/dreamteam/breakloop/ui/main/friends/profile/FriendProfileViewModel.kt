package com.dreamteam.breakloop.ui.main.friends.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.local.cache.FriendProfileMemoryCache
import com.dreamteam.breakloop.data.remote.ProfileRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.repository.FriendProfileRepositoryImpl
import com.dreamteam.breakloop.data.system.ConnectivityDataSource
import com.dreamteam.breakloop.domain.FriendProfileResult
import com.dreamteam.breakloop.ui.main.offline.components.ActivityDetailUiState
import com.dreamteam.breakloop.ui.navigation.FriendProfileRoute
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendProfileViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val friendUid = savedStateHandle.toRoute<FriendProfileRoute>().uid
    private val repository = FriendProfileRepositoryImpl(
        ProfileRemoteDataSource(RetrofitInstance.profileApi, FirebaseAuth.getInstance()),
        dao = AppDatabase.getInstance(application.applicationContext).friendProfileDao(),
        memoryCache = FriendProfileMemoryCache,
        connectivity = ConnectivityDataSource(application.applicationContext)
    )

    private val _state = MutableStateFlow<FriendProfileUiState>(FriendProfileUiState.Loading)
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = FriendProfileUiState.Loading
            _state.value = when (
                val r= repository.getFriendProfile(friendUid)
            ) {
                is FriendProfileResult.Fresh -> FriendProfileUiState.Content(r.profile, isStale = false)
                is FriendProfileResult.Stale -> FriendProfileUiState.Content(r.profile, isStale = true)
                FriendProfileResult.NeverLoaded -> FriendProfileUiState.NeverLoadedOffline
                FriendProfileResult.NotFound -> FriendProfileUiState.NotFound

            }
        }
    }
}
