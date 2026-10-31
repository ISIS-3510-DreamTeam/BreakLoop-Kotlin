package com.dreamteam.breakloop.ui.main.friends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.remote.FriendsRemoteDataSource
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.data.repository.FriendsRepositoryImpl
import com.dreamteam.breakloop.data.system.ConnectivityDataSource
import com.dreamteam.breakloop.data.system.FriendsLocalDataSource
import com.dreamteam.breakloop.domain.enums.AddFriendResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val localDataSource = FriendsLocalDataSource(application.applicationContext)
    private val repository = FriendsRepositoryImpl(
        remoteDataSource = FriendsRemoteDataSource(RetrofitInstance.friendsApi, FirebaseAuth.getInstance()),
        localDataSource = localDataSource,
        connectivity = ConnectivityDataSource(application.applicationContext)
    )

    private val _state = MutableStateFlow(
        FriendsUiState(
            myFriendCode = null,
            friends = emptyList(),
            lastUpdated = null,
            pendingCodes = emptySet(),
            isOffline = !repository.isOnline(),
            isLoading = false,
            message = null
        )
    )
    val state: StateFlow<FriendsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeConnection().collect { online ->
                _state.update { it.copy(isOffline = !online) }
                if (online) {
                    loadFriends(forceRefresh = true)
                }
            }
        }
        loadFriends()
    }

    fun loadFriends(forceRefresh: Boolean = false) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _state.update { it.copy(message = "Log in to see your friends") }
            return
        }
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, message = null) }

        viewModelScope.launch {
            try {
                val stored = repository.getStoredFriends(uid)
                if (stored != null) {
                    val (lista, at) = stored
                    if (lista != null) {
                        _state.update {
                            it.copy(
                                friends = lista,
                                lastUpdated = at ?: it.lastUpdated
                            )
                        }
                    }
                }
                val pending = localDataSource.getPendingCodes(uid)
                _state.update { it.copy(pendingCodes = pending) }

                val online = repository.isOnline()
                coroutineScope {
                    val perfilDeferred = async { repository.ensureProfile(uid) }
                    if (online && (forceRefresh || !repository.isCacheFresh(uid))) {
                        val invalidos = repository.syncPendingCodes(uid)
                        val listaDeferred = async { repository.refreshFriends(uid) }
                        val nuevaLista = listaDeferred.await()
                        val ahora = System.currentTimeMillis()
                        val updatedPending = localDataSource.getPendingCodes(uid)
                        val msg = if (invalidos.isNotEmpty()) {
                            "We couldn't add: ${invalidos.joinToString(", ")}"
                        } else null

                        _state.update {
                            it.copy(
                                friends = nuevaLista,
                                lastUpdated = ahora,
                                pendingCodes = updatedPending,
                                message = msg ?: it.message
                            )
                        }
                    }
                    val perfil = perfilDeferred.await()
                    _state.update {
                        it.copy(
                            myFriendCode = perfil?.friendCode ?: it.myFriendCode
                        )
                    }
                }
                if (!online && _state.value.friends.isEmpty()) {
                    _state.update { it.copy(message = "You're offline. Connect to see your friends") }
                }
            } catch (e: Exception) {
                val msg = if (_state.value.friends.isEmpty()) {
                    "We couldn't load your friends"
                } else {
                    "Showing your last saved list"
                }
                _state.update { it.copy(message = msg) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addFriend(code: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            _state.update { it.copy(message = "Log in to add friends") }
            return
        }
        val trimmedCode = code.trim().uppercase()
        if (trimmedCode.isEmpty()) {
            _state.update { it.copy(message = "Enter a code") }
            return
        }
        if (trimmedCode == _state.value.myFriendCode) {
            _state.update { it.copy(message = "You can't add yourself") }
            return
        }
        if (!repository.isOnline()) {
            viewModelScope.launch {
                val newPending = repository.queuePendingCode(uid, trimmedCode)
                _state.update {
                    it.copy(
                        pendingCodes = newPending,
                        message = "We'll add them when you're back online"
                    )
                }
            }
            return
        }
        viewModelScope.launch {
            try {
                when (repository.addFriend(trimmedCode)) {
                    AddFriendResult.ADDED -> {
                        _state.update { it.copy(message = "Friend added!") }
                        loadFriends(forceRefresh = true)
                    }
                    AddFriendResult.NOT_FOUND -> {
                        _state.update { it.copy(message = "No user has that code") }
                    }
                    AddFriendResult.OWN_CODE -> {
                        _state.update { it.copy(message = "You can't add yourself") }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(message = "We couldn't add your friend. Try again") }
            }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null) }
    }
}