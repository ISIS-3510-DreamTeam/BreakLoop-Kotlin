package com.dreamteam.breakloop.ui.layout

import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun TopBar(navController: NavController,
           title: String) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.smal
    )
}