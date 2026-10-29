package com.dreamteam.breakloop.ui.main.stats.flaggedapps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.FlaggedApp
import com.dreamteam.breakloop.domain.InstalledApp
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

@Composable
fun FlaggedAppsRoute(
    onBack: () -> Unit,
    viewModel: FlaggedAppsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    FlaggedAppsScreen(
        state = state,
        onBack = onBack,
        onCheckboxClick = viewModel::onCheckboxClick,
        onEditClick = viewModel::onEditClick,
        onDismissEdit = viewModel::onDismissEdit,
        onLimitSave = viewModel::onLimitSave
    )
}

@Composable
fun FlaggedAppsScreen(
    state: FlaggedAppsUiState,
    onBack: () -> Unit,
    onCheckboxClick: (FlaggedAppRow) -> Unit,
    onEditClick: (FlaggedAppRow) -> Unit,
    onDismissEdit: () -> Unit,
    onLimitSave: (hours: Int, minutes: Int, noLimit: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
    ) {
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
                text = "DISTRACTING APPS",
                style = Typography.labelSmall,
                color = ColorPalette.Neutral.t700
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Choose your focus apps",
                style = Typography.titleLarge,
                color = ColorPalette.Neutral.t1000
            )
            Text(
                text = "Pick the apps you'd like to use more intentionally, then set a daily limit that feels right.",
                style = Typography.bodyMedium,
                color = ColorPalette.Neutral.t700
            )
        }

        when (state) {
            FlaggedAppsUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ColorPalette.SpicyPaprika.t500)
            }

            FlaggedAppsUiState.Empty -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "We couldn't find any apps to choose from. Install a few and come back.",
                    style = Typography.bodyMedium,
                    color = ColorPalette.Neutral.t700,
                    textAlign = TextAlign.Center
                )
            }

            is FlaggedAppsUiState.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp, vertical = 8.dp
                    )
                ) {
                    items(state.flaggedApps, key = { it.app.packageName }) { row ->
                        AppRow(
                            row = row,
                            onCheckboxClick = { onCheckboxClick(row) },
                            onEditClick = { onEditClick(row) }
                        )
                        HorizontalDivider(color = ColorPalette.Neutral.t100)
                    }
                }
                state.editing?.let { row ->
                    DailyLimitSheet(
                        row = row,
                        onDismiss = onDismissEdit,
                        onSave = onLimitSave
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRow(
    row: FlaggedAppRow,
    onCheckboxClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val flagged = row.flagged != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(onClick = onCheckboxClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(row.app.packageName, size = 44.dp, label = row.app.label)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.app.label,
                style = Typography.titleSmall,
                color = ColorPalette.Neutral.t1000,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (flagged) {
                Text(
                    text = "${formatDailyLimit(row.flagged?.dailyLimitMin)} · Edit",
                    style = Typography.labelMedium,
                    color = ColorPalette.SpicyPaprika.t700,
                    modifier = Modifier
                        .heightIn(min = 32.dp)
                        .clickable(onClick = onEditClick)
                        .padding(top = 6.dp)
                )
            }
        }
        Checkbox(
            checked = flagged,
            onCheckedChange = { onCheckboxClick() },
            colors = CheckboxDefaults.colors(
                checkedColor = ColorPalette.SpicyPaprika.t500,
                uncheckedColor = ColorPalette.Neutral.t500,
                checkmarkColor = ColorPalette.Neutral.Snow
            )
        )
    }
}

private val previewRows = listOf(
    FlaggedAppRow(InstalledApp("a", "Instagram"), FlaggedApp("a", 90, 0L)),
    FlaggedAppRow(InstalledApp("b", "TikTok"), FlaggedApp("b", null, 0L)),
    FlaggedAppRow(InstalledApp("c", "Calculator"), null),
)

@Preview(showBackground = true)
@Composable
private fun FlaggedAppsScreenContentPreview() = FlaggedAppsScreen(
    state = FlaggedAppsUiState.Content(previewRows, null),
    onBack = {}, onCheckboxClick = {}, onEditClick = {}, onDismissEdit = {}, onLimitSave = { _, _, _ -> }
)

@Preview(showBackground = true)
@Composable
private fun FlaggedAppsScreenEmptyPreview() = FlaggedAppsScreen(
    state = FlaggedAppsUiState.Empty,
    onBack = {}, onCheckboxClick = {}, onEditClick = {}, onDismissEdit = {}, onLimitSave = { _, _, _ -> }
)

@Preview(showBackground = true)
@Composable
private fun FlaggedAppsScreenSheetPreview() = FlaggedAppsScreen(
    state = FlaggedAppsUiState.Content(previewRows, previewRows.first()),
    onBack = {}, onCheckboxClick = {}, onEditClick = {}, onDismissEdit = {}, onLimitSave = { _, _, _ -> }
)
