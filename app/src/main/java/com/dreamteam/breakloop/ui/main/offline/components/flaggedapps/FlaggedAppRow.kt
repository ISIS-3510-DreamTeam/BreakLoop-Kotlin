package com.dreamteam.breakloop.ui.main.offline.components.flaggedapps
import com.dreamteam.breakloop.domain.FlaggedApp
import com.dreamteam.breakloop.domain.InstalledApp

data class FlaggedAppRow (
    val app: InstalledApp,
    val flagged: FlaggedApp?
)
