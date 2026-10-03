package com.dreamteam.breakloop.ui.main.stats

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.dreamteam.breakloop.ui.util.formatDuration
import kotlin.math.abs

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = viewModel(),
    onDeepStatsClick: () -> Unit = {}
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
            onDeepStatsClick = onDeepStatsClick,
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Encabezado
        BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(ColorPalette.GoldenOrange.t100, RoundedCornerShape(12.dp))
                        .border(2.dp, ColorPalette.Neutral.t1000, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Insights,
                        contentDescription = null,
                        tint = ColorPalette.GoldenOrange.t500,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "YOUR PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorPalette.GoldenOrange.t500
                    )
                    Text(
                        text = "Mindful reduction metrics",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ColorPalette.Neutral.t1000
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "vs baseline, this week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorPalette.Neutral.t700
                )
                TextButton(onClick = onDeepStatsClick) {
                    Text(text = "Deep Stats →")
                }
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = Icons.Outlined.CalendarToday,
                    label = "WEEKLY SCREEN RHYTHM"
                )
                WeeklyBars(weeklySummary = content.weeklySummary)

                val averageMs = content.weeklySummary.weeklyAverageMs
                val averageText = if (averageMs != null) formatDuration(averageMs) else "—"
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = null,
                        tint = ColorPalette.Neutral.t700,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Average: $averageText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorPalette.Neutral.t700
                    )
                }
            }
        }

        // Tamed Distraction Loops
        BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = Icons.Outlined.LockReset,
                    label = "TAMED DISTRACTION LOOPS"
                )
                Text(
                    text = "Available when you mark your special apps",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPalette.Neutral.t700
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    content.apps.forEachIndexed { index, app ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(ColorPalette.Neutral.t100, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Apps,
                                        contentDescription = null,
                                        tint = ColorPalette.Neutral.t700,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ColorPalette.Neutral.t1000
                                )
                            }
                            Text(
                                text = formatDuration(app.foregroundMs),
                                style = MaterialTheme.typography.labelSmall,
                                color = ColorPalette.SpicyPaprika.t700
                            )
                        }
                        if (index != content.apps.lastIndex) {
                            HorizontalDivider(color = ColorPalette.Neutral.t100)
                        }
                    }
                }
            }
        }

        // Reducción vs. baseline
        BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
            val reduction = content.reduction
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = if (reduction != null && reduction.changePercent < 0) {
                        Icons.Outlined.TrendingDown
                    } else {
                        Icons.Outlined.TrendingUp
                    },
                    label = "REDUCTION VS. BASELINE"
                )

                if (reduction == null) {
                    Text(
                        text = "Calculating your baseline...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorPalette.Neutral.t700
                    )
                } else {
                    val direction = if (reduction.changePercent < 0) "lower" else "higher"
                    Text(
                        text = "Your weekly average is ${abs(reduction.changePercent)}% $direction than your baseline",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorPalette.Neutral.t1000
                    )
                    Text(
                        text = "Baseline: ${formatDuration(reduction.baselineMs)} / day",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorPalette.Neutral.t700
                    )
                    LinearProgressIndicator(
                        progress = { reduction.progressTowardTarget },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = ColorPalette.Fern.t500,
                        trackColor = ColorPalette.Neutral.t100
                    )
                    Text(
                        text = "Progress toward the -20% goal",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorPalette.Neutral.t700
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ColorPalette.Neutral.t700,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ColorPalette.Neutral.t700
        )
    }
}