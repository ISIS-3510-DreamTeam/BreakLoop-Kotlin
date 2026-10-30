package com.dreamteam.breakloop.ui.main.offline.components.activeactivity

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

/**
 * Franja bajo la barra superior que avisa de la actividad en curso o terminada.
 *
 * Sin estado: solo pinta [state]. Si el estado pasa a Hidden se conserva el último contenido
 * visible mientras dura la animación de salida, para que no desaparezca de golpe.
 */
@Composable
fun ActiveActivityBanner(
    state: ActiveActivityUiState,
    onClick: (activityId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lastVisible = remember { mutableStateOf(state) }
    if (state !is ActiveActivityUiState.Hidden) lastVisible.value = state

    AnimatedVisibility(
        visible = state !is ActiveActivityUiState.Hidden,
        modifier = modifier,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        when (val shown = lastVisible.value) {
            ActiveActivityUiState.Hidden -> Unit
            is ActiveActivityUiState.Running -> BannerStrip(
                text = "${shown.title} · ${formatRemaining(shown.remainingSeconds)} left",
                description = "${shown.title}, ${formatRemaining(shown.remainingSeconds)} left. Open activity",
                containerColor = ColorPalette.GoldenOrange.t100,
                accentColor = ColorPalette.SpicyPaprika.t700,
                icon = Icons.Outlined.Timer,
                onClick = { onClick(shown.activityId) },
            )

            is ActiveActivityUiState.Finished -> BannerStrip(
                text = "${shown.title} done! Tap to log it",
                description = "${shown.title} done. Tap to log it",
                containerColor = ColorPalette.Fern.t100,
                accentColor = ColorPalette.Fern.t500,
                icon = Icons.Outlined.CheckCircle,
                onClick = { onClick(shown.activityId) },
            )
        }
    }
}

@Composable
private fun BannerStrip(
    text: String,
    description: String,
    containerColor: androidx.compose.ui.graphics.Color,
    accentColor: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        color = containerColor,
        border = BorderStroke(1.5.dp, ColorPalette.Neutral.t1000),
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = description }
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = text,
                    style = Typography.labelSmall,
                    color = ColorPalette.Neutral.t1000,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ColorPalette.Neutral.t700,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

private fun formatRemaining(seconds: Int): String =
    "%02d:%02d".format(seconds / 60, seconds % 60)

@Preview(name = "Running · light", showBackground = true)
@Preview(name = "Running · dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RunningPreview() {
    BreakLoopTheme {
        ActiveActivityBanner(
            state = ActiveActivityUiState.Running("1", "Evening walk", 754),
            onClick = {},
        )
    }
}

@Preview(name = "Finished · light", showBackground = true)
@Preview(name = "Finished · dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinishedPreview() {
    BreakLoopTheme {
        ActiveActivityBanner(
            state = ActiveActivityUiState.Finished("1", "Evening walk"),
            onClick = {},
        )
    }
}
