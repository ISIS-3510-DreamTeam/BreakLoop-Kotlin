package com.dreamteam.breakloop.ui.main.achievements.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.data.system.NetworkChecker
import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel

/** Shown once per unlocked achievement. Works offline: the unlock is calculated on the phone. */
@Composable
fun CelebrationDialog(
    progress: AchievementProgress,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val online = remember { NetworkChecker.isOnline(context) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ACHIEVEMENT UNLOCKED!", style = MonoLabel) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AchievementMedal(progress = progress, modifier = Modifier.size(120.dp))
                Spacer(Modifier.height(12.dp))
                Text(
                    text = progress.achievement.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = ColorPalette.Neutral.t1000,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = progress.achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = ColorPalette.Neutral.t850,
                )
                if (!online) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "You're offline. It's saved on this phone and will be added to your account when you're back online.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = ColorPalette.Neutral.t700,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Awesome!") }
        },
    )
}