package com.dreamteam.breakloop.ui.main.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.WeeklySummary

@Composable
fun WeeklyBars(
    weeklySummary: WeeklySummary,
    modifier: Modifier = Modifier
) {
    val max = weeklySummary.bars.mapNotNull { it.screenTimeMs }.maxOrNull() ?: 1L
    Row(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (bar in weeklySummary.bars) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                val fraction = (bar.screenTimeMs ?: 0L).toFloat() / max.toFloat()
                Box(
                    modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight(fraction*0.85f).background(
                        MaterialTheme.colorScheme.primary)
                ) {

                }
                val dayInitialLetter = bar.dayOfWeek.name.first().toString()
                Text(text = dayInitialLetter)
            }
        }
    }
}
