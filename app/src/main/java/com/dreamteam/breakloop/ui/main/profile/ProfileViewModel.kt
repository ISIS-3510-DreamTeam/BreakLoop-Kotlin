package com.dreamteam.breakloop.ui.main.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.files.ProfilePhotoDataSource
import com.dreamteam.breakloop.data.local.preferences.ProfilePreferences
import com.dreamteam.breakloop.data.repository.ProfileRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel (
    application: Application
): AndroidViewModel(application) {
    private val repository = ProfileRepositoryImpl(
        ProfilePreferences(application.applicationContext),
        ProfilePhotoDataSource(application.applicationContext),
        WorkManagerSyncScheduler(application.applicationContext)
    )

    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val _isSaving = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)

    val state: StateFlow<ProfileUiState> = (
        if (uid ==null) flowOf(ProfileUiState.NotLoggedIn)
        else
            combine(
                repository.observeMyProfile(uid),
                _isSaving,
                _message
            ) { profile, isSaving, message ->
                ProfileUiState.Content(
                    profile,
                    isSaving,
                    message
                )
            }
            ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
                ProfileUiState.Loading)

    fun onSaveName( name: String) {
        val uid = uid ?: return
        if (name.trim().isEmpty()) {
            _message.value = "Name can't be empty"
        } else
            viewModelScope.launch {
                repository.updateName(uid, name.trim())
            }
    }

    fun onPhotoPicked(uri: Uri) {
        val uid = uid ?: return
        viewModelScope.launch {
            _isSaving.value = true
            val ok = repository.updatePhoto(uid,uri)
            _isSaving.value = false
            if (!ok) {
                _message.value = "We couldn't save your photo"
            }
        }
    }

}