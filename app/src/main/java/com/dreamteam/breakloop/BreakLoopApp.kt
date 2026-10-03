package com.dreamteam.breakloop

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.dreamteam.breakloop.data.auth.FirebaseAuthRepository
import com.dreamteam.breakloop.ui.layout.NavBar
import com.dreamteam.breakloop.ui.layout.TopBar
import com.dreamteam.breakloop.ui.navigation.AppNavHost
import com.dreamteam.breakloop.ui.navigation.currentTopLevelDestination
import com.dreamteam.breakloop.ui.navigation.navigateToTopLevel
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme

@Composable
fun BreakLoopApp() {
    val authRepository = remember { FirebaseAuthRepository() }
    val authState = remember { authRepository.authState() }
    val user by authState.collectAsStateWithLifecycle(initialValue = authRepository.currentUser)
    val isLoggedIn = user != null

    key(isLoggedIn) {
        BreakLoopAppContent(isLoggedIn = isLoggedIn, onSignOut = authRepository::signOut)
    }
}

@Composable
fun BreakLoopAppContent(isLoggedIn: Boolean, onSignOut: () -> Unit = {},) {
    val navController = rememberNavController()
    val currentTab = navController.currentTopLevelDestination()

    Scaffold(
        topBar = {
            if (currentTab != null) {
                TopBar(
                    title = stringResource(currentTab.label),
                    onSignOut = onSignOut,
                )
            }
        },
        bottomBar = {
            if (currentTab != null) {
                NavBar(
                    currentTab = currentTab,
                    onTabSelected = navController::navigateToTopLevel,
                )
            }
        }
    ) { padding ->
        AppNavHost(navController, isLoggedIn, Modifier.padding(padding))
    }
}