package com.dreamteam.breakloop

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.dreamteam.breakloop.ui.layout.NavBar
import com.dreamteam.breakloop.ui.navigation.AppNavHost
import com.dreamteam.breakloop.ui.navigation.currentTopLevelDestination
import com.dreamteam.breakloop.ui.navigation.navigateToTopLevel
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme

@Composable
fun BreakLoopApp(isLoggedId: Boolean = false) {
    val navController = rememberNavController()
    val currentTab = navController.currentTopLevelDestination()

    Scaffold(
        bottomBar = {
            if (currentTab != null) {
                NavBar(
                    currentTab = currentTab,
                    onTabSelected = navController::navigateToTopLevel,
                )
            }
        }
    ) { padding ->
        AppNavHost(navController, isLoggedId, Modifier.padding(padding))
    }
}

@Preview(showBackground = true)
@Composable
fun BreakLoopAppLoggedInPreview() {
    BreakLoopTheme {
        BreakLoopApp(isLoggedId = true)
    }
}