package com.dreamteam.breakloop.ui.main.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography
import kotlin.math.abs
import kotlin.math.roundToInt

/** Tarjeta destacada con el dato principal del día. */
@Composable
fun TodayHeroCard(
    value: String,
    changePercent: Int?,
    modifier: Modifier = Modifier
) {
    BreakLoopCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = ColorPalette.GoldenOrange.t100
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.PhoneAndroid,
                contentDescription = null,
                tint = ColorPalette.SpicyPaprika.t700,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "TODAY'S SCREEN TIME",
                style = Typography.labelSmall,
                color = ColorPalette.SpicyPaprika.t700
            )
        }
        Text(
            text = value,
            style = Typography.headlineLarge,
            color = ColorPalette.Neutral.t1000,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )
        ChangeChip(changePercent)
    }
}

@Composable
private fun ChangeChip(changePercent: Int?) {
    val lower = changePercent != null && changePercent <= 0
    val container = when {
        changePercent == null -> ColorPalette.Neutral.Snow
        lower -> ColorPalette.Fern.t100
        else -> ColorPalette.SpicyPaprika.t100
    }
    val content = when {
        changePercent == null -> ColorPalette.Neutral.t700
        lower -> ColorPalette.Fern.t500
        else -> ColorPalette.SpicyPaprika.t700
    }
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .background(container, shape)
            .border(1.5.dp, ColorPalette.Neutral.t1000, shape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (changePercent != null) {
            Icon(
                imageVector = if (lower) Icons.AutoMirrored.Outlined.TrendingDown else Icons.AutoMirrored.Outlined.TrendingUp,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = if (changePercent == null) "A fresh start!" else "${abs(changePercent)}% vs last week",
            style = Typography.labelSmall,
            color = content
        )
    }
}

/** Métrica compacta con ícono de color, pensada para ir tres en una fila. */
@Composable
fun MiniMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconBackground: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    BreakLoopCard(modifier = modifier, elevated = false, contentPadding = 12.dp) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(iconBackground, RoundedCornerShape(8.dp))
                .border(1.5.dp, ColorPalette.Neutral.t1000, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = value,
            style = Typography.headlineSmall,
            color = if (value == "—") ColorPalette.Neutral.t500 else ColorPalette.Neutral.t1000,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = label,
            style = Typography.labelSmall,
            color = ColorPalette.Neutral.t700
        )
    }
}

/** Barra de progreso hecha de bloques cuadrados, estilo barra de vida. */
@Composable
fun PixelProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    segments: Int = 20,
    filledColor: Color = ColorPalette.Fern.t500,
    emptyColor: Color = ColorPalette.Neutral.t100
) {
    val filled = (progress.coerceIn(0f, 1f) * segments).roundToInt()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, ColorPalette.Neutral.t1000, RoundedCornerShape(4.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(segments) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .background(if (index < filled) filledColor else emptyColor)
            )
        }
    }
}
