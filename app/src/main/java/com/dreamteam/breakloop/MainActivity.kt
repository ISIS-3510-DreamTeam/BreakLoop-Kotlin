package com.dreamteam.breakloop

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.dreamteam.breakloop.BreakLoopApp
import com.dreamteam.breakloop.data.auth.FirebaseAuthRepository
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            BreakLoopTheme {
                BreakLoopApp( )
            }
        }
        val repo = FirebaseAuthRepository()
        lifecycleScope.launch {
            Log.d("AuthTest", "Before: ${repo.currentUser}")
            val result = repo.signUp("test1@breakloop.dev", "password123")
            Log.d("AuthTest", "SignUp: $result")
            Log.d("AuthTest", "After: ${repo.currentUser}")
            val cause = result.exceptionOrNull()?.cause
            Log.d("AuthTest", "Cause: ${cause?.javaClass?.name} - ${cause?.message}")
        }
    }
}
// TODO: acá debe vivir el splash

