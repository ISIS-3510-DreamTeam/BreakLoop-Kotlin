package com.dreamteam.breakloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.dreamteam.breakloop.ui.navigation.AppNavigation
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            BreakLoopTheme {
                AppNavigation(navController = navController, isLoggedIn = false)
            }
        }
    }
}

