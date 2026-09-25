package com.dreamteam.breakloop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

fun NavController.navigateToTopLevel(tab: TopLevelDestination){
    navigate(tab.route){
        popUpTo(graph.findStartDestination().id) { saveState = true}
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun NavController.currentTopLevelDestination(): TopLevelDestination? {
    val destination = currentBackStackEntryAsState().value?.destination ?: return null
    return TopLevelDestination.entries.firstOrNull { tab ->
        destination.hierarchy.any { it.hasRoute(tab.route::class) }
    }
}