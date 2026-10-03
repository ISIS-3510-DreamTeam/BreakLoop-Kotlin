package com.dreamteam.breakloop.ui.main.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.WeeklySummary
import com.dreamteam.breakloop.ui.theme.ColorPalette
import java.time.LocalDate


private val BarShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
private val LabelSpacing = 8.dp

@Composable
fun WeeklyBars(
    weeklySummary: WeeklySummary,
    modifier: Modifier = Modifier,
    goalMs: Long? = null
) {
    val barsMax = weeklySummary.bars.mapNotNull { it.screenTimeMs }.maxOrNull() ?: 1L
    // Si hay meta, la escala la incluye para que la línea no se salga del gráfico.
    val max = (if (goalMs != null) maxOf(barsMax, goalMs) else barsMax).coerceAtLeast(1L)

    val today = LocalDate.now().dayOfWeek

    // Alto de las letras, para que la línea de meta use la misma base que las barras.
    var labelHeightPx by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxWidth().height(144.dp)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            for (bar in weeklySummary.bars) {
                val isToday = bar.dayOfWeek == today
                val screenTimeMs = bar.screenTimeMs

                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(LabelSpacing, Alignment.Bottom)
                ) {
                    // Zona de la barra: todo el alto que queda encima de la letra.
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (screenTimeMs == null) {
                            // Día sin datos: barrita mínima, sin sombra.
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(ColorPalette.Neutral.t100, BarShape)
                            )
                        } else {
                            val fraction = screenTimeMs.toFloat() / max.toFloat()
                            val shadowOffset = if (isToday) 2.dp else 1.dp
                            val barColor =
                                if (isToday) ColorPalette.SpicyPaprika.t500 else ColorPalette.GoldenOrange.t500

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(fraction * 0.85f)
                            ) {
                                // Sombra desplazada
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .offset(shadowOffset, shadowOffset)
                                        .background(ColorPalette.Neutral.t1000, BarShape)
                                )
                                // Barra
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(barColor, BarShape)
                                )
                            }
                        }
                    }

                    Text(
                        text = bar.dayOfWeek.name.first().toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) ColorPalette.SpicyPaprika.t500 else ColorPalette.Neutral.t700,
                        fontWeight = if (isToday) FontWeight.Bold else null,
                        modifier = Modifier.onSizeChanged { labelHeightPx = it.height }
                    )
                }
            }
        }

        if (goalMs != null) {
            Canvas(modifier = Modifier.matchParentSize()) {
                // Base de las barras = alto total menos la letra y su separación.
                val barAreaHeight = size.height - labelHeightPx - LabelSpacing.toPx()
                val y = barAreaHeight * (1f - goalMs.toFloat() / max.toFloat() * 0.85f)
                drawLine(
                    color = ColorPalette.SpicyPaprika.t500.copy(alpha = 0.7f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
            }
        }
    }
}