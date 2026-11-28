package com.dreamteam.breakloop.ui.main.achievements.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.achievements.components.AchievementMedal
import com.dreamteam.breakloop.ui.main.achievements.formatUnlockDate
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel
import com.dreamteam.breakloop.ui.theme.Typography
import com.dreamteam.breakloop.ui.components.OfflineNotice

@Composable
fun AchievementDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementDetailViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val connectivity by viewModel.connectivityStatus.collectAsStateWithLifecycle()
    val progress = (state as? AchievementDetailUiState.Content)?.progress
    val offlineMessage =
        if (progress != null && progress.unlocked && !progress.synced) {
            "No internet connection. This achievement is saved on this phone and will be added to your account when you're back online."
        } else {
            "No internet connection. Your progress is calculated on this phone; the medal image will load when you're back online."
        }

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
                text = "ACHIEVEMENT",
                style = Typography.labelSmall,
                color = ColorPalette.Neutral.t700
            )
        }
        OfflineNotice(status = connectivity, offlineMessage = offlineMessage)
        when (val current = state) {
            is AchievementDetailUiState.Loading -> LoadingContent()
            is AchievementDetailUiState.NotFound -> Text(
                text = "This achievement is not available.",
                modifier = Modifier.padding(16.dp),
                color = ColorPalette.Neutral.t700
            )
            is AchievementDetailUiState.Content -> DetailContent(current.progress)
        }
    }
}

@Composable
private fun DetailContent(progress: AchievementProgress) {
    var imageFailed by remember { mutableStateOf(false) }
    val achievement = progress.achievement

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
            AchievementMedal(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                onImageError = { imageFailed = true }
            )
            if (imageFailed) {
                Text(
                    text = "The medal image will appear when you're online.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = ColorPalette.Neutral.t700,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = achievement.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ColorPalette.Neutral.t1000
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = ColorPalette.Neutral.t850
            )
            Spacer(Modifier.height(16.dp))

            if (progress.unlocked) {
                Text(
                    text = "Unlocked on ${formatUnlockDate(progress.unlockedAt)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ColorPalette.Fern.t500
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (progress.synced) "Saved to your account."
                    else "Saved on this phone. It will be added to your account when you're online.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPalette.Neutral.t700
                )
            } else {
                val shown = progress.current.coerceAtMost(achievement.target)
                Text(text = "PROGRESS", style = MonoLabel)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress.fraction },
                    modifier = Modifier.fillMaxWidth(),
                    color = ColorPalette.GoldenOrange.t500,
                    trackColor = ColorPalette.Neutral.t100,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$shown/${achievement.target} · ${achievement.target - shown} to go",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPalette.Neutral.t700
                )
            }
        }
    }
}