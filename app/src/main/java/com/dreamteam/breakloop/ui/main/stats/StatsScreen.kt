package com.dreamteam.breakloop.ui.main.stats

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.stats.components.MetricCard
import com.dreamteam.breakloop.ui.main.stats.components.WeeklyBars
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.ColorPalette.Neutral.Snow
import com.dreamteam.breakloop.ui.theme.Typography
import com.dreamteam.breakloop.ui.util.formatDuration
import kotlin.math.abs

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = viewModel(),
    onDeepStatsClick: @Composable () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit){
        viewModel.refresh()
        onPauseOrDispose {  }
    }
    when (val state = uiState) {
        StatsUiState.Loading -> LoadingContent(modifier = modifier)
        StatsUiState.NoPermission -> NoPermissionContent(modifier = modifier)
        is StatsUiState.Content -> StatsContent(
            content = state,
            onDeepStatsClick = onDeepStatsClick as () -> Unit,
            modifier = modifier
        )
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
            color = ColorPalette.GoldenOrange.t500
        )
        Button(
            onClick = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
            colors = ButtonDefaults.buttonColors( containerColor = ColorPalette.SpicyPaprika.t500)
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
            .background(Snow)
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
                    style = Typography.headlineMedium
                )
                Text(
                    text = "Mindful reduction metrics vs baseline",
                    style = Typography.bodyMedium,
                    color = ColorPalette.GoldenOrange.t500
                )
            }
            TextButton(onClick = onDeepStatsClick) {
                Text(text = "Deep Stats →")
            }
        }

        // Tarjetas
        val percentChange = content.weeklySummary.changeVsLastWeekPercent
        val changeText = if (percentChange == null) "good start!" else "${percentChange}% vs last week"

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                label = "Today's screen time",
                value = formatDuration(content.totalMs),
                supportingText = changeText,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Focus minutes",
                value = "—",
                supportingText = "sprint 3**",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                label = "Current streak",
                value = "—",
                supportingText = "days under target",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Best streak",
                value = "—",
                supportingText = "your record, congrats!",
                modifier = Modifier.weight(1f)
            )
        }

        // Gráfica semanal
        BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Weekly Screen Rhythm",
                style = Typography.displaySmall
            )
            WeeklyBars(weeklySummary = content.weeklySummary)

            val averageMs = content.weeklySummary.weeklyAverageMs
            val averageText = if (averageMs != null) formatDuration(averageMs) else "—"
            Text(
                text = "Average: $averageText",
                style = Typography.bodyMedium
            )


        }

        // Tamed Distraction Loops
        BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Tamed Distraction Loops",
                style = Typography.displaySmall
            )
            Text(
                text = "Available when you mark your special apps",
                style = Typography.bodySmall,
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

        BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
            // Reducción vs. baseline
            Text(
                text = "Reduction vs. baseline:",
                style = Typography.labelSmall
            )

            val reduction = content.reduction
            if (reduction ==null){
                Text(text= "Calculating your baseline...")
            } else {
                val direction = if (reduction.changePercent < 0) "lower" else "higher"
                Text(text = "Your weekly average is ${abs(reduction.changePercent)}% $direction than your baseline",
                    style = Typography.bodyMedium,
                    color = ColorPalette.Neutral.t1000
                )
                Text(text = "Baseline: ${formatDuration(reduction.baselineMs)} / day",
                    style = Typography.bodySmall,
                    color = ColorPalette.Neutral.t700)
                LinearProgressIndicator(
                    progress = { reduction.progressTowardTarget },
                    modifier = Modifier.fillMaxWidth(),
                    color = ColorPalette.Fern.t500,
                    trackColor = ColorPalette.Neutral.t100
                )

                Text(
                    text = "Progress toward the -20% goal",
                    style = Typography.labelSmall,
                    color = ColorPalette.Neutral.t700
                )
            }
        }
    }
}