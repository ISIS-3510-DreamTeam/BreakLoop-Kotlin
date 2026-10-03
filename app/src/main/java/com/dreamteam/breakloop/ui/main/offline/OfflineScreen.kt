package com.dreamteam.breakloop.ui.main.offline

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopChip
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.offline.components.ActivityCard
import com.dreamteam.breakloop.ui.main.offline.components.CategoryChips
import com.dreamteam.breakloop.ui.main.offline.components.SuggestedCard
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.ColorPalette.Neutral.Snow

@Composable
fun OfflineScreen(
    modifier: Modifier = Modifier,
    viewModel: OfflineViewModel = viewModel(),
    onActivityClick: (String, Boolean) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        OfflineUiState.Loading -> LoadingContent(modifier = modifier)
        is OfflineUiState.Content -> OfflineContent(
            state,
            viewModel::onAvailableTimeSelected,
            viewModel::onCategorySelected,
            onActivityClick = onActivityClick,
            onLocationPermissionResult = viewModel::onLocationPermissionResult
        )

    }
}

@Composable
fun OfflineContent(
    state: OfflineUiState.Content,
    onAvailableTimeSelected: (Int) -> Unit,
    onCategorySelected: (ActivityCategory?) -> Unit,
    onActivityClick: (String, Boolean) -> Unit,
    onLocationPermissionResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { onLocationPermissionResult() }

    Column(
        modifier = modifier.fillMaxSize()
            .background(Snow)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        BreakLoopCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(ColorPalette.SpicyPaprika.t100, RoundedCornerShape(12.dp))
                        .border(2.dp, ColorPalette.Neutral.t1000, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WifiOff,
                        contentDescription = null,
                        tint = ColorPalette.SpicyPaprika.t700,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "OFFLINE MODE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorPalette.SpicyPaprika.t700
                    )
                    Text(
                        text = "What will you do offline?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ColorPalette.Neutral.t1000
                    )
                }
            }
            Text(
                text = "Pick something screen-free to recharge.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorPalette.Neutral.t700,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (state.needsLocationPermission) {
            BreakLoopCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Want weather-aware suggestions?",
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorPalette.Neutral.t1000
                )
                Text(
                    text = "We only use your approximate location to check the weather.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorPalette.Neutral.t700
                )
                BreakLoopChip(
                    text = "Enable location",
                    selected = true,
                    onClick = { launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        SuggestedCard(
            suggestion = state.suggestion,
            availableMin = state.availableMin,
            isWeatherAvailable = state.isWeatherAvailable,
            onTimeSelected = onAvailableTimeSelected,
            onActivityClick = { id -> onActivityClick(id, true) }
        )
        CategoryChips(
            selectedCategory = state.selectedCategory,
            onCategorySelected = onCategorySelected
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ListAlt,
                    contentDescription = null,
                    tint = ColorPalette.Neutral.t700,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "ALL ACTIVITIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorPalette.Neutral.t700
                )
            }
            for (activity in state.activities) {
                ActivityCard(
                    activity = activity,
                    onClick = { onActivityClick(activity.id, false) }
                )
            }
        }
    }
}