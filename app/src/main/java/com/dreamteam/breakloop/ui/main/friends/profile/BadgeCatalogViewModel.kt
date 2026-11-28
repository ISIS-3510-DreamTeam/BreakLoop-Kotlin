package com.dreamteam.breakloop.ui.main.friends.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.achievement.AchievementRepository
import com.dreamteam.breakloop.domain.Achievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Names and medal images for the badge ids of a friend. Null while loading. */
class BadgeCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AchievementRepository(application)

    private val _catalog = MutableStateFlow<List<Achievement>?>(null)
    val catalog: StateFlow<List<Achievement>?> = _catalog.asStateFlow()

    init {
        // network, then the saved copy, then the one bundled in the app, so it also works offline
        viewModelScope.launch { _catalog.value = repository.loadCatalog().achievements }
    }
}
