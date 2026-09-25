package com.dreamteam.breakloop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation

@Composable
fun AppNavHost(navController: NavHostController, isLoggedIn: Boolean, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) MainGraph else AuthGraph,
        modifier = modifier
    ) {
        navigation<AuthGraph>(startDestination = Splash) {
            composable<Splash> {
                // TODO: splash screen
            }
            composable<Login> {
                // TODO: login screen
            }
        }
        navigation<MainGraph>(startDestination = Home) {
            composable<Home> {
                // TODO: home screen
            }
            composable<Focus> {
                // TODO: focus screen
            }
        }
    }
}