package com.dreamteam.breakloop.ui.main.friends.profile

import com.dreamteam.breakloop.ui.components.StreakIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.FriendProfile
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopChip
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.components.PixelArt
import com.dreamteam.breakloop.ui.components.PixelIcon
import com.dreamteam.breakloop.ui.main.profile.components.ProfileAvatar
import com.dreamteam.breakloop.ui.main.profile.components.ProfileHeader
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun FriendProfileScreen(
    onBack: () -> Unit,
    viewModel: FriendProfileViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // the header stays outside the when, so going back works in every state
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
    ) {
        ProfileHeader(title = "Friend profile", onBack = onBack)

        when (val current = state) {
            FriendProfileUiState.Loading -> LoadingContent()
            FriendProfileUiState.NeverLoadedOffline -> EmptyProfileContent(
                icon = Icons.Outlined.CloudOff,
                message = "You're offline and haven't opened this profile before. Connect to see it.",
                onRetry = viewModel::load,
            )
            FriendProfileUiState.NotFound -> EmptyProfileContent(
                icon = Icons.Outlined.PersonOff,
                message = "This friend doesn't have a profile yet.",
            )
            is FriendProfileUiState.Content -> FriendProfileContent(
                profile = current.profile,
                isStale = current.isStale,
                onRetry = viewModel::load,
            )
        }
    }
}

@Composable
private fun FriendProfileContent(
    profile: FriendProfile,
    isStale: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isStale) {
            StaleNotice(fetchedAt = profile.fetchedAt, onRetry = onRetry)
        }

        ProfileAvatar(
            name = profile.displayName,
            localPhotoPath = null,
            size = 168.dp,
            remotePhotoUrl = profile.photoUrl,
        )

        Text(
            text = profile.displayName,
            style = MaterialTheme.typography.headlineMedium,
            color = ColorPalette.Neutral.t1000,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(label = "Current streak", modifier = Modifier.weight(1f)) {
                StreakIcon()
                StatValue("${profile.streak} d")
            }
            StatCard(label = "Pet level", modifier = Modifier.weight(1f)) {
                PixelIcon(PixelArt.PAW, size = 24.dp)
                StatValue("${profile.petLevel}")
            }
        }

        BadgesCard(profile.badges)
    }
}

@Composable
private fun StatCard(
    label: String,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit,
) {
    BreakLoopCard(modifier = modifier, elevated = false, contentPadding = 12.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SectionLabel(label)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                value()
            }
        }
    }
}

@Composable
private fun StatValue(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = ColorPalette.Neutral.t1000,
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = ColorPalette.Neutral.t700,
    )
}

@Composable
private fun BadgesCard(badges: List<String>) {
    BreakLoopCard(modifier = Modifier.fillMaxWidth(), elevated = false) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionLabel("Badges")
            if (badges.isEmpty()) {
                Text(
                    "No badges yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorPalette.Neutral.t700,
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    badges.forEach { id ->
                        BreakLoopChip(
                            text = badgeLabel(id),
                            selected = false,
                            onClick = {},
                            modifier = Modifier,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StaleNotice(fetchedAt: Long, onRetry: () -> Unit) {
    BreakLoopCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ColorPalette.GoldenOrange.t100,
        elevated = false,
        contentPadding = 12.dp,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "You're offline. Showing what you saw ${timeAgo(fetchedAt)}",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorPalette.Neutral.t1000,
            )
            BreakLoopButton(text = "Retry", onClick = onRetry)
        }
    }
}

@Composable
private fun EmptyProfileContent(
    icon: ImageVector,
    message: String,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ColorPalette.Neutral.t500,
            modifier = Modifier.size(64.dp),
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorPalette.Neutral.t700,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            BreakLoopButton(text = "Retry", onClick = onRetry)
        }
    }
}

// "streak_7" -> "Streak 7"
private fun badgeLabel(id: String): String =
    id.split("_")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

private fun timeAgo(fetchedAt: Long): String {
    val minutes = ((System.currentTimeMillis() - fetchedAt) / 60_000).coerceAtLeast(0)
    val days = minutes / (60 * 24)
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        days < 1 -> "${minutes / 60} h ago"
        days == 1L -> "1 day ago"
        else -> "$days days ago"
    }
}
