package com.dreamteam.breakloop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.ColorPalette

// Tiny pixel art drawn from text: '#' is the main color, 'o' the highlight, '.' is empty.
enum class PixelArt(val rows: List<String>, val main: Color, val highlight: Color) {
    PAW(
        rows = listOf(
            "..##...##..",
            "..##...##..",
            "##.......##",
            "##.......##",
            "....###....",
            "...#####...",
            "..#######..",
            "..#######..",
            "...##.##...",
        ),
        main = ColorPalette.Fern.t500,
        highlight = ColorPalette.Fern.t300,
    ),
}

@Composable
fun PixelIcon(art: PixelArt, modifier: Modifier = Modifier, size: Dp = 16.dp) {
    Canvas(modifier.size(size)) {
        val columns = art.rows.first().length
        // whole pixels keep the edges crisp, without seams between cells
        val cell = (this.size.minDimension / maxOf(columns, art.rows.size)).toInt().coerceAtLeast(1)
        val left = ((this.size.width - cell * columns) / 2).toInt()
        val top = ((this.size.height - cell * art.rows.size) / 2).toInt()

        art.rows.forEachIndexed { y, row ->
            row.forEachIndexed { x, pixel ->
                if (pixel != '.') {
                    drawRect(
                        color = if (pixel == 'o') art.highlight else art.main,
                        topLeft = Offset((left + x * cell).toFloat(), (top + y * cell).toFloat()),
                        size = Size(cell.toFloat(), cell.toFloat()),
                    )
                }
            }
        }
    }
}
