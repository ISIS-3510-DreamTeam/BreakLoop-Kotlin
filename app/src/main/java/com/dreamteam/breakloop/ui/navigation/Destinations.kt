package com.dreamteam.breakloop.ui.navigation

import android.R
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
@Serializable data class OfflineActivityDetail(val activityId: String)
@Serializable data object Friends {

}