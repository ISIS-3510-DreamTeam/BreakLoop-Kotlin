package com.dreamteam.breakloop.ui.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun AppLayout(
    navController: NavController,
    content: @Composable () -> Unit
) {
    var showBottomBar by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(

        )
    }

}