package com.dreamteam.breakloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun BreakLoopCard(
    modifier: Modifier = Modifier,
    containerColor: Color = ColorPalette.Neutral.Snow,
    elevated: Boolean = true,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val ink = ColorPalette.Neutral.t1000

    Box(modifier) {
        // sombra sólida estilo pixel art: grande en tarjetas destacadas, pequeña en las secundarias
        val shadow = if (elevated) 4.dp else 2.dp
        Box(
            Modifier
                .matchParentSize()
                .offset(x = shadow, y = shadow)
                .background(ink, shape)
        )
        // card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor, shape)
                .border(2.dp, ink, shape)
                .padding(contentPadding),
            content = content,
        )
    }
}