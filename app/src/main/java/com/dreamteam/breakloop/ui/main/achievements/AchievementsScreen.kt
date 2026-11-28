package com.dreamteam.breakloop.ui.main.achievements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.domain.CatalogSource
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.achievements.components.AchievementMedal
import com.dreamteam.breakloop.ui.main.achievements.components.CelebrationDialog
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography
import com.dreamteam.breakloop.ui.components.OfflineNotice

/** Grid of achievements: locked (gray, with progress) and unlocked (with date). */
@Composable
fun AchievementsScreen(
    onBack: () -> Unit,
    onAchievementClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val celebration by viewModel.celebration.collectAsStateWithLifecycle()
    val connectivity by viewModel.connectivityStatus.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = ColorPalette.Neutral.t1000
                )
            }
            Text(
                text = "ACHIEVEMENTS",
                style = Typography.labelSmall,
                color = ColorPalette.Neutral.t700
            )
        }

        OfflineNotice(
            status = connectivity,
            offlineMessage = offlineMessage(state),
        )

        when (val current = state) {
            is AchievementsUiState.Loading -> LoadingContent()
            is AchievementsUiState.Error -> ErrorContent(onRetry = viewModel::load)
            is AchievementsUiState.Content -> AchievementsGrid(current, onAchievementClick)
        }
    }

    celebration?.let { CelebrationDialog(progress = it, onDismiss = viewModel::dismissCelebration) }
}

@Composable
private fun AchievementsGrid(
    content: AchievementsUiState.Content,
    onAchievementClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {

        if (content.items.isEmpty()) {
            Text(
                text = "No achievements available yet.",
                modifier = Modifier.padding(16.dp),
                color = ColorPalette.Neutral.t700
            )
            return@Column
        }

        Text(
            text = "${content.unlockedCount} of ${content.items.size} unlocked",
            style = MaterialTheme.typography.bodyMedium,
            color = ColorPalette.Neutral.t700,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(content.items, key = { it.achievement.id }) { progress ->
                AchievementTile(
                    progress = progress,
                    onClick = { onAchievementClick(progress.achievement.id) }
                )
            }
        }
    }
}

@Composable
private fun AchievementTile(progress: AchievementProgress, onClick: () -> Unit) {
    BreakLoopCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        AchievementMedal(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(96.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = progress.achievement.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = ColorPalette.Neutral.t1000,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        if (progress.unlocked) {
            Text(
                text = "Unlocked ${formatUnlockDate(progress.unlockedAt)}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = ColorPalette.Fern.t500,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LinearProgressIndicator(
                progress = { progress.fraction },
                modifier = Modifier.fillMaxWidth(),
                color = ColorPalette.GoldenOrange.t500,
                trackColor = ColorPalette.Neutral.t100,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${progress.current.coerceAtMost(progress.achievement.target)}/${progress.achievement.target}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = ColorPalette.Neutral.t700,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Couldn't load your achievements.", color = ColorPalette.Neutral.t1000)
            TextButton(onClick = onRetry) { Text("Try again") }
        }
    }
}


/** Own offline message of this screen, depending on where the catalog came from. */
private fun offlineMessage(state: AchievementsUiState): String =
    if (state is AchievementsUiState.Content && state.source == CatalogSource.BUNDLED) {
        "No internet connection. Showing the built-in achievements; medal images will appear when you're back online."
    } else {
        "No internet connection. Showing your saved achievements. New unlocks are saved on this phone."
    }