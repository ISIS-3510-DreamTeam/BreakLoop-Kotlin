package com.dreamteam.breakloop.ui.main.stats.deepstats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.layout.TopBar
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.ColorPalette.Neutral.Snow
import com.dreamteam.breakloop.ui.theme.Typography

private val StatsTitleFont = FontFamily.Monospace
private val StatsBodyFont = FontFamily.SansSerif
private const val DAILY_PICKUP_BASELINE = 86

@Composable
fun DeepStatsScreen(
    modifier: Modifier = Modifier,
    viewModel: DeepStatsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Snow)
    ) {

        when (val state = uiState) {
            DeepStatsUiState.Loading -> {
                LoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            is DeepStatsUiState.Content -> {
                DeepStatsContent(
                    content = state,
                    onBackClick = onBackClick,
                    modifier = Modifier.weight(1f)
                )
            }

            is DeepStatsUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Snow),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = ColorPalette.SpicyPaprika.t500
        )
    }
}

@Composable
private fun DeepStatsContent(
    content: DeepStatsUiState.Content,
    onBackClick: () -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBackClick
            ) {
                Text(
                    text = "← Back",
                    style = Typography.titleMedium.copy(fontFamily = StatsTitleFont),
                    color = ColorPalette.SpicyPaprika.t700,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Detailed Statistics",
                style = Typography.headlineMedium.copy(fontFamily = StatsTitleFont),
                color = ColorPalette.Neutral.t1000,
                fontWeight = FontWeight.Bold
            )
        }

        MindfulUnlockCard(
            mindfulPercentage = content.mindfulPercentage,
            impulsivePercentage = content.impulsivePercentage
        )

        DailyPickupCard(
            dailyPickups = content.dailyPickups,
            averageIntervalMinutes = content.avgIntervalMinutes
        )
    }
}

@Composable
private fun MindfulUnlockCard(
    mindfulPercentage: Int,
    impulsivePercentage: Int
) {
    BreakLoopCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "MINDFUL UNLOCK INTENTIONALITY",
            style = Typography.displaySmall.copy(fontFamily = StatsTitleFont),
            color = ColorPalette.SpicyPaprika.t700,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )

        Spacer(
            modifier = Modifier.size(20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(135.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = ColorPalette.Neutral.t100,
                    strokeWidth = 9.dp
                )

                CircularProgressIndicator(
                    progress = {
                        mindfulPercentage
                            .coerceIn(0, 100) / 100f
                    },
                    modifier = Modifier.fillMaxSize(),
                    color = ColorPalette.SpicyPaprika.t500,
                    strokeWidth = 9.dp
                )

                Text(
                    text = "$mindfulPercentage%",
                    style = Typography.headlineMedium.copy(fontFamily = StatsTitleFont),
                    color = ColorPalette.SpicyPaprika.t700,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.width(24.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$mindfulPercentage% intentional unlocks",
                    style = Typography.titleLarge.copy(fontFamily = StatsTitleFont),
                    color = ColorPalette.Neutral.t1000,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "Only $impulsivePercentage% were impulsive reflex pick-ups.",
                    style = Typography.bodyLarge.copy(fontFamily = StatsBodyFont),
                    color = ColorPalette.Neutral.t700
                )
            }
        }
    }
}

@Composable
private fun DailyPickupCard(
    dailyPickups: Int,
    averageIntervalMinutes: Int
) {
    BreakLoopCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "DAILY PICKUP COUNT",
            style = Typography.displaySmall.copy(fontFamily = StatsTitleFont),
            color = ColorPalette.SpicyPaprika.t700,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.size(20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = dailyPickups.toString(),
                style = Typography.displayLarge.copy(fontFamily = StatsTitleFont),
                color = ColorPalette.Neutral.t1000,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "pickups",
                style = Typography.headlineMedium.copy(fontFamily = StatsTitleFont),
                color = ColorPalette.Neutral.t1000,
                modifier = Modifier.padding(bottom = 7.dp)
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            val difference = DAILY_PICKUP_BASELINE - dailyPickups
            val comparisonText = when {
                difference > 0 -> "$difference fewer"
                difference < 0 -> "${-difference} more"
                else -> "Same as"
            }

            Text(
                text = comparisonText,
                style = Typography.titleMedium.copy(fontFamily = StatsTitleFont),
                color = if (difference >= 0) ColorPalette.Fern.t500 else ColorPalette.SpicyPaprika.t700,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 7.dp)
            )
        }

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        Text(
            text = "Compared with a reference of $DAILY_PICKUP_BASELINE phone checks per day, " +
                    "you made ${dailyPickups} checks today.",
            style = Typography.bodyLarge.copy(fontFamily = StatsBodyFont),
            color = ColorPalette.Neutral.t700
        )

        Spacer(
            modifier = Modifier.size(8.dp)
        )

    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Snow)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = Typography.bodyLarge.copy(fontFamily = StatsBodyFont),
            color = ColorPalette.Neutral.t1000,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=412dp,height=915dp,dpi=440"
)
@Composable
private fun DeepStatsScreenPreview() {
    MaterialTheme {
        DeepStatsScreenPreviewContent()
    }
}

@Composable
private fun DeepStatsScreenPreviewContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Snow)
    ) {
        TopBar(
            title = "Deep Stats",
            onSignOut = {}
        )

        DeepStatsContent(
            content = DeepStatsUiState.Content(
                dailyPickups = 15,
                avgIntervalMinutes = 53,
                baselineText = "-71 vs. daily reference",
                mindfulPercentage = 62,
                impulsivePercentage = 38
            ),
            onBackClick = {},
            modifier = Modifier.weight(1f)
        )
    }
}
