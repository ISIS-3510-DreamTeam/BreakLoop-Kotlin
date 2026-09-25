package com.dreamteam.breakloop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dreamteam.breakloop.ui.account.navigation.AuthRoutes
import com.dreamteam.breakloop.ui.account.navigation.authGraph

object Routes {
    const val AUTH_GRAPH = "auth_graph"
    const val MAIN_GRAPH = "main_graph"

    const val HOME = "home"
    const val PROFILE = "profile"
    const val FOCUS = "focus"
}

fun NavHostController.safeNavigate(route: String, baseRoute: String) {
    if (currentBackStackEntry?.destination?.route != route) {
        navigate(route) {
            popUpTo(baseRoute) { inclusive = false }
            launchSingleTop = true
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.MAIN_GRAPH else Routes.AUTH_GRAPH
    ) {
        navigation(
            route = Routes.AUTH_GRAPH,
            startDestination = AuthRoutes.SPLASH
        ) {
            authGraph(navController)
        }
        navigation(
            route = Routes.MAIN_GRAPH,
            startDestination = Routes.HOME
        ) {
            composable(Routes.HOME) {
                // TODO: Agregar main
            }
            composable(Routes.PROFILE) {
                // TODO: Agregar perfil
            }
            composable(Routes.FOCUS) {
                // TODO: Agregar FOCUS
            }
        }
    }
}