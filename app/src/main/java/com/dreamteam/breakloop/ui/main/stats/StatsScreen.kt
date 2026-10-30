package com.dreamteam.breakloop.ui.main.stats

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.dreamteam.breakloop.ui.main.stats.components.MiniMetricCard
import com.dreamteam.breakloop.ui.main.stats.components.PixelProgressBar
import com.dreamteam.breakloop.ui.main.stats.components.TodayHeroCard
import com.dreamteam.breakloop.ui.main.stats.flaggedapps.FlaggedAppRow
import com.dreamteam.breakloop.ui.main.stats.flaggedapps.FlaggedAppsCard
import com.dreamteam.breakloop.ui.main.stats.components.WeeklyBars
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.ColorPalette.Neutral.Snow
import com.dreamteam.breakloop.ui.util.formatDuration
import kotlin.math.abs

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = viewModel(),
    onDeepStatsClick: () -> Unit = {},
    onConfigureFlaggedAppsClick: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val flaggedRows by viewModel.flaggedRows.collectAsStateWithLifecycle()
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
            flaggedRows = flaggedRows,
            onConfigureFlaggedAppsClick = onConfigureFlaggedAppsClick,
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
    flaggedRows: List<FlaggedAppRow>?,
    onConfigureFlaggedAppsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Snow)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Encabezado
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "YOUR PROGRESS",
                style = MaterialTheme.typography.labelSmall,
                color = ColorPalette.SpicyPaprika.t700
            )
            Text(
                text = "Mindful reduction metrics",
                style = MaterialTheme.typography.titleLarge,
                color = ColorPalette.Neutral.t1000
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "vs baseline, this week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorPalette.Neutral.t700
                )
                TextButton(onClick = onDeepStatsClick) {
                    Text(
                        text = "Deep Stats →",
                        style = MaterialTheme.typography.labelLarge,
                        color = ColorPalette.SpicyPaprika.t700
                    )
                }
            }
        }

        // Dato principal del día
        TodayHeroCard(
            value = formatDuration(content.totalMs),
            changePercent = content.weeklySummary.changeVsLastWeekPercent
        )

        // Métricas secundarias
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniMetricCard(
                icon = Icons.Outlined.Timer,
                label = "Focus minutes",
                value = "—",
                iconBackground = ColorPalette.Fern.t100,
                iconTint = ColorPalette.Fern.t500,
                modifier = Modifier.weight(1f)
            )
            MiniMetricCard(
                icon = Icons.Outlined.LocalFireDepartment,
                label = "Current streak",
                value = "—",
                iconBackground = ColorPalette.SpicyPaprika.t100,
                iconTint = ColorPalette.SpicyPaprika.t500,
                modifier = Modifier.weight(1f)
            )
            MiniMetricCard(
                icon = Icons.Outlined.EmojiEvents,
                label = "Best streak",
                value = "—",
                iconBackground = ColorPalette.GoldenOrange.t100,
                iconTint = ColorPalette.GoldenOrange.t500,
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
        FlaggedAppsCard(
            flaggedRows = flaggedRows,
            onConfigureClick = onConfigureFlaggedAppsClick
        )

        // Reducción vs. baseline
        BreakLoopCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
            val reduction = content.reduction
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    icon = if (reduction != null && reduction.changePercent < 0) {
                        Icons.AutoMirrored.Outlined.TrendingDown
                    } else {
                        Icons.AutoMirrored.Outlined.TrendingUp
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
                    PixelProgressBar(progress = reduction.progressTowardTarget)
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