package com.dreamteam.breakloop.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.ui.theme.ColorPalette

// the app's streak flame, so every screen draws the same one
@Composable
fun StreakIcon(modifier: Modifier = Modifier, size: Dp = 22.dp) {
    Icon(
        painter = painterResource(R.drawable.ic_streak),
        contentDescription = null,
        tint = ColorPalette.Neutral.t1000,
        modifier = modifier.size(size),
    )
}
