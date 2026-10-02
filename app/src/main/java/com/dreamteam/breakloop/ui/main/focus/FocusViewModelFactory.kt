package com.dreamteam.breakloop.ui.main.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dreamteam.breakloop.data.local.repository.FocusSessionRepository

class FocusViewModelFactory(
    private val focusSessionRepository: FocusSessionRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
            return FocusViewModel(
                focusSessionRepository = focusSessionRepository,
                appContext = context.applicationContext
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}