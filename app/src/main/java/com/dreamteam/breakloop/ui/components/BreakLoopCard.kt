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
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun BreakLoopCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val ink = ColorPalette.Neutral.t1000

    Box(modifier) {
        // make shadow?
        Box(
            Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(ink, shape)
        )
        // card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorPalette.Neutral.Snow, shape)
                .border(2.dp, ink, shape)
                .padding(16.dp),
            content = content,
        )
    }
}