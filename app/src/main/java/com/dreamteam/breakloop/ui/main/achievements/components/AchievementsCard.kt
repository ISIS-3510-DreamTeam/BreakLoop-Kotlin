package com.dreamteam.breakloop.ui.main.achievements.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.main.achievements.AchievementsUiState
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel

/** Home card that opens the achievements screen. */
@Composable
fun AchievementsCard(
    state: AchievementsUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BreakLoopCard(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        contentPadding = 12.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.EmojiEvents,
                contentDescription = null,
                tint = ColorPalette.Neutral.t1000,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "ACHIEVEMENTS", style = MonoLabel)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle(state),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorPalette.Neutral.t1000,
                    maxLines = 1,
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Open achievements",
                tint = ColorPalette.Neutral.t1000,
            )
        }
    }
}

private fun subtitle(state: AchievementsUiState): String = when (state) {
    is AchievementsUiState.Loading -> "Checking your progress…"
    is AchievementsUiState.Error -> "See your medals"
    is AchievementsUiState.Content -> "${state.unlockedCount} of ${state.items.size} unlocked"
}