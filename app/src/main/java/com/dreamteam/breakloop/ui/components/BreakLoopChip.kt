package com.dreamteam.breakloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun BreakLoopChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
){
    val shape = RoundedCornerShape(50)
    val background = if (selected) ColorPalette.SpicyPaprika.t500 else ColorPalette.Neutral.Snow
    val textColor = if (selected) ColorPalette.Neutral.Snow else ColorPalette.Neutral.t1000

    Box(
        modifier = modifier
            .border(2.dp, ColorPalette.Neutral.t1000, shape)
            .background(background, shape)
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MonoLabel,
            color = textColor
        )
    }
}