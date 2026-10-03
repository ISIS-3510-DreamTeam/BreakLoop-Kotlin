package com.dreamteam.breakloop.ui.main.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.domain.GoalProgress
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.MonoLabel
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.util.formatDuration

@Composable
fun ScreenTimeGoalCard(
    usedMs: Long,
    progress: GoalProgress,
    onEditGoal: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val (message, color) = when (progress) {
        GoalProgress.NotSet -> "No daily goal yet. Tap the gear to set one." to ColorPalette.Neutral.t700
        is GoalProgress.OnTrack -> { val percent = progress.usedMs * 100 / progress.goalMs
            "$percent% of your goal · ${formatDuration(progress.remainingMs)} left" to ColorPalette.Fern.t500 }
        is GoalProgress.NearLimit -> { val percent = progress.usedMs * 100 / progress.goalMs
            "$percent% of your goal · ${formatDuration(progress.remainingMs)} left" to ColorPalette.GoldenOrange.t500 }
        is GoalProgress.Exceeded -> { val percent = progress.usedMs * 100 / progress.goalMs
            "$percent% over your goal · ${formatDuration(progress.exceededMs)} left" to ColorPalette.SpicyPaprika.t500 }
    }

    BreakLoopCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "TODAY'S SCREEN TIME",
                style = MonoLabel,
                modifier = Modifier.weight(1f), //right
            )
            IconButton(onClick = onEditGoal) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "Edit daily goal",
                    tint = ColorPalette.Neutral.t1000,
                )
            }
        }
        Text(text = formatDuration(usedMs), style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(8.dp))
        Text(text = message, color = color, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun OnTrackPreview() {
    BreakLoopTheme {
        ScreenTimeGoalCard(
            usedMs = 60 * 60 * 1000L,
            progress = GoalProgress.OnTrack(usedMs = 60 * 60 * 1000L, goalMs = 3 * 60 * 60 * 1000L),
            onEditGoal = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}