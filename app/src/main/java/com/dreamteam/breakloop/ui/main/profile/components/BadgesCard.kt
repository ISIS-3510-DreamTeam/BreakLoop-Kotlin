package com.dreamteam.breakloop.ui.main.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.main.achievements.AchievementsUiState
import com.dreamteam.breakloop.ui.main.achievements.components.AchievementMedal
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel

private const val MEDALS_SHOWN = 3

/** Profile preview of the achievements: latest medals first, then the closest to unlock. */
@Composable
fun BadgesCard(
    state: AchievementsUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BreakLoopCard(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        elevated = false,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("BADGES", style = MonoLabel, modifier = Modifier.weight(1f))
                if (state is AchievementsUiState.Content) {
                    Text(
                        text = "${state.unlockedCount} of ${state.items.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorPalette.Neutral.t700,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Open achievements",
                    tint = ColorPalette.Neutral.t1000,
                )
            }
            when (state) {
                AchievementsUiState.Loading -> Message("Checking your progress…")
                AchievementsUiState.Error -> Message("Couldn't load your badges.")
                is AchievementsUiState.Content -> Medals(state.items)
            }
        }
    }
}

@Composable
private fun Medals(items: List<AchievementProgress>) {
    if (items.isEmpty()) {
        Message("No achievements available yet.")
        return
    }
    val unlocked = items.filter { it.unlocked }.sortedByDescending { it.unlockedAt ?: 0L }
    val locked = items.filterNot { it.unlocked }.sortedByDescending { it.fraction }
    val shown = (unlocked + locked).take(MEDALS_SHOWN)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        shown.forEach { progress ->
            MedalTile(progress = progress, modifier = Modifier.weight(1f))
        }
        // keeps the medals the same width when the catalog has fewer than three
        repeat(MEDALS_SHOWN - shown.size) { Spacer(Modifier.weight(1f)) }
    }
}

/** Medal with its name; locked ones also show how far along they are. */
@Composable
fun MedalTile(progress: AchievementProgress, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AchievementMedal(progress = progress, modifier = Modifier.size(72.dp))
        Text(
            text = progress.achievement.name,
            style = MaterialTheme.typography.labelMedium,
            color = ColorPalette.Neutral.t1000,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (!progress.unlocked) {
            Text(
                text = "${progress.current.coerceAtMost(progress.achievement.target)}/${progress.achievement.target}",
                style = MaterialTheme.typography.bodySmall,
                color = ColorPalette.Neutral.t700,
            )
        }
    }
}

@Composable
private fun Message(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = ColorPalette.Neutral.t700,
    )
}
