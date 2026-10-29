package com.dreamteam.breakloop.ui.main.offline.components.flaggedapps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.repository.FlaggedAppsRepositoryImpl
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.domain.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlaggedAppsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _installedApps = MutableStateFlow<List<InstalledApp>?>(null)
    private val _editingPackage = MutableStateFlow<String?>(null)

    private val db = AppDatabase.getInstance(application.applicationContext)

    private val installedAppsDataSource = InstalledAppsDataSource(application.applicationContext)
    private val flaggedAppsRepositoryImpl = FlaggedAppsRepositoryImpl(flaggedAppsDao = db.flaggedAppDao())

    val uiState: StateFlow<FlaggedAppsUiState> = combine(
        _installedApps,
        flaggedAppsRepositoryImpl.getFlaggedApps(),
        _editingPackage
    ) { installed, flagged, editing ->
        if (installed == null) return@combine FlaggedAppsUiState.Loading

        if (installed.isEmpty()) return@combine FlaggedAppsUiState.Empty

        val flaggedByPackage = flagged.associateBy { it.packageName }

        val unSortedRows = installed.map { app ->
            FlaggedAppRow(
                app = app,
                flagged = flaggedByPackage[app.packageName]
            )
        }

        val (flaggedRows, unflaggedRows) = unSortedRows.partition { it.flagged != null }
        val rows = flaggedRows.sortedBy { it.app.label.lowercase() } +
                unflaggedRows.sortedBy { it.app.label.lowercase() }

        val editingRow = editing?.let { pkg -> rows.find { it.app.packageName == pkg } }

        FlaggedAppsUiState.Content(rows, editingRow)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FlaggedAppsUiState.Loading
    )

    init {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                installedAppsDataSource.obtainAppsToFlag()
            }
            _installedApps.value = list
            flaggedAppsRepositoryImpl.deleteUninstalledApps(list.map { it.packageName }.toSet())
        }
    }

    fun onCheckboxClick(row: FlaggedAppRow) {
        viewModelScope.launch {
            if (row.flagged != null) {
                flaggedAppsRepositoryImpl.deletePackage(row.app.packageName)
            } else {
                flaggedAppsRepositoryImpl.addFlaggedApp(row.app.packageName, null)
            }
        }
    }

    fun onEditClick(row: FlaggedAppRow) {
        _editingPackage.value = row.app.packageName
    }

    fun onDismissEdit() {
        _editingPackage.value = null
    }
}