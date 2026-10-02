package com.dreamteam.breakloop.ui.main.stats

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.ui.main.stats.components.WeeklyBars
import com.dreamteam.breakloop.ui.util.formatDuration

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = viewModel(),
    onDeepStatsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        StatsUiState.Loading -> LoadingContent(modifier = modifier)
        StatsUiState.NoPermission -> NoPermissionContent(modifier = modifier)
        is StatsUiState.Content -> StatsContent(
            content = state,
            onDeepStatsClick = onDeepStatsClick,
            modifier = modifier
        )
    }
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NoPermissionContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "We don't have permission to access your data and it is necessary to measure your screentime ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Button(
            onClick = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
        ) {
            Text(text = "Give access")
        }
    }
}

@Composable
fun StatsContent(
    content: StatsUiState.Content,
    onDeepStatsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Track your screen time and habits",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onDeepStatsClick) {
                Text(text = "Deep Stats →")
            }
        }

        // Tarjetas
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Today's screen time",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = formatDuration(content.totalMs),
                    style = MaterialTheme.typography.headlineLarge
                )
                val percentChange = content.weeklySummary.changeVsLastWeekPercent
                val changeText = if (percentChange == null) {
                    "—"
                } else {
                    "${percentChange}% vs last week"
                }
                Text(
                    text = changeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Focus minutes",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "—",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Current streak",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "—",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Best streak",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "—",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        // Gráfica semanal
        Text(
            text = "Weekly Screen Rhythm",
            style = MaterialTheme.typography.titleLarge
        )
        WeeklyBars(weeklySummary = content.weeklySummary)

        val averageMs = content.weeklySummary.weeklyAverageMs
        val averageText = if (averageMs != null) formatDuration(averageMs) else "—"
        Text(
            text = "Average: $averageText",
            style = MaterialTheme.typography.bodyMedium
        )

        // Reducción vs. baseline
        Text(
            text = "Reduction vs. baseline: —",
            style = MaterialTheme.typography.bodyMedium
        )

        // Tamed Distraction Loops
        Text(
            text = "Tamed Distraction Loops",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Disponible cuando marques tus apps distractoras",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        for (app in content.apps) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = app.packageName)
                Text(text = formatDuration(app.foregroundMs))
            }
        }
    }
}
