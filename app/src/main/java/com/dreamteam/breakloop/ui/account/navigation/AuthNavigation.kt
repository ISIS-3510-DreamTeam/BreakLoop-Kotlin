package com.dreamteam.breakloop.ui.account.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object AuthRoutes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val SPLASH = "splash"

}

fun NavGraphBuilder.authGraph(navController: NavController) {
    composable(AuthRoutes.SPLASH) {
        // TODO: poner acá la ruta para componente splash
    }
    composable(AuthRoutes.LOGIN) {
        // TOO: poner ruta a componente login
    }
    composable(AuthRoutes.SIGNUP) {
        // TODO: poner ruta a componente signup
    }
}