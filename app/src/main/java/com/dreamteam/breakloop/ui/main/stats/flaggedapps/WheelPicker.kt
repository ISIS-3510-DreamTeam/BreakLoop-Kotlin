package com.dreamteam.breakloop.ui.main.stats.flaggedapps

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

private val ItemHeight = 40.dp
private const val VisibleItems = 5
private const val Padding = VisibleItems / 2

/**
 * Selector de rueda estilo iOS. Usa ítems en blanco arriba y abajo para que el primer y
 * el último valor puedan quedar centrados; el valor centrado es firstVisibleItemIndex
 * (redondeado según el desplazamiento).
 */
@Composable
fun WheelPicker(
    range: IntRange,
    initialValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val values = remember(range) { range.toList() }
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = (initialValue - range.first).coerceIn(0, values.lastIndex)
    )
    val selectedIndex by remember {
        derivedStateOf {
            val extra = if (state.firstVisibleItemScrollOffset > ItemHeightPx(state) / 2) 1 else 0
            (state.firstVisibleItemIndex + extra).coerceIn(0, values.lastIndex)
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { selectedIndex }.collect { onValueChange(values[it]) }
    }

    val fade = ColorPalette.Neutral.Snow
    Box(modifier = modifier.height(ItemHeight * VisibleItems)) {
        // franja de selección
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ItemHeight)
                .background(ColorPalette.Neutral.t100, RoundedCornerShape(10.dp))
        )
        LazyColumn(
            state = state,
            userScrollEnabled = enabled,
            flingBehavior = rememberSnapFlingBehavior(state),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Padding) { Box(Modifier.height(ItemHeight)) }
            items(values.size) { index ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .height(ItemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%02d".format(values[index]),
                        style = if (selected) Typography.headlineSmall else Typography.titleMedium,
                        color = if (selected) ColorPalette.Neutral.t1000 else ColorPalette.Neutral.t500,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(Padding) { Box(Modifier.height(ItemHeight)) }
        }
        // degradado arriba y abajo
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(ItemHeight * 2)
                .background(Brush.verticalGradient(listOf(fade, fade.copy(alpha = 0f))))
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(ItemHeight * 2)
                .background(Brush.verticalGradient(listOf(fade.copy(alpha = 0f), fade)))
        )
    }
}

private fun ItemHeightPx(state: androidx.compose.foundation.lazy.LazyListState): Int =
    state.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: Int.MAX_VALUE
