package com.dreamteam.breakloop.ui.navigation

import kotlinx.serialization.Serializable

@Serializable data object AuthGraph
@Serializable data object Splash
@Serializable data object Login

@Serializable data object MainGraph
@Serializable data object Home
@Serializable data object Profile
@Serializable data object Focus

//@Serializable data class ProfileDetail(val name: String)
@Serializable data object Stats {

}
@Serializable data object DeepStats
@Serializable data object Offline {

}
@Serializable data class OfflineActivityDetail(val activityId: String, val fromSuggestion: Boolean=false)
@Serializable data object Friends {

}
@Serializable data object Stats
@Serializable data object DeepStats
@Serializable data object Offline
@Serializable data object Friends