package com.dreamteam.breakloop.ui.main.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoTitle

@Composable
fun FriendCodeCard(code: String?, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    var copied by remember(code) { mutableStateOf(false) }

    BreakLoopCard(modifier = modifier, elevated = false, contentPadding = 12.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .clickable(enabled = code != null, onClickLabel = "Copy friend code") {
                    if (code != null) {
                        clipboard.setText(AnnotatedString(code))
                        copied = true
                    }
                },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "FRIEND CODE",
                style = MaterialTheme.typography.labelSmall,
                color = ColorPalette.Neutral.t700,
            )
            if (code == null) {
                Text(
                    "Connect to get your code",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPalette.Neutral.t700,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(text = code, style = MonoTitle, color = ColorPalette.SpicyPaprika.t700)
                Text(
                    text = if (copied) "Copied!" else "Tap to copy",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPalette.Neutral.t700,
                )
            }
        }
    }
}
