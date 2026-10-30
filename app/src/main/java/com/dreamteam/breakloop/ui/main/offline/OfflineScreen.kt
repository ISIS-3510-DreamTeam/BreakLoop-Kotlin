package com.dreamteam.breakloop.ui.main.offline

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopChip
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.offline.components.ActivityCard
import com.dreamteam.breakloop.ui.main.offline.components.CategoryChips
import com.dreamteam.breakloop.ui.main.offline.components.SuggestedCard
import com.dreamteam.breakloop.ui.main.offline.components.TimeSelector
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
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
            viewModel::onPlaceSelected,
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
    onPlaceSelected: (PlaceLabel?) -> Unit,
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
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "SCREEN-FREE SESSION",
                style = MaterialTheme.typography.labelSmall,
                color = ColorPalette.SpicyPaprika.t700
            )
            Text(
                text = "What will you do offline?",
                style = MaterialTheme.typography.titleLarge,
                color = ColorPalette.Neutral.t1000
            )
            Text(
                text = "How much time do you have right now?",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorPalette.Neutral.t700
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
        TimeSelector(
            availableMin = state.availableMin,
            onTimeSelected = onAvailableTimeSelected
        )
        PlaceSelector(
            selectedPlace = state.selectedPlace,
            onPlaceSelected = onPlaceSelected
        )
        SuggestedCard(
            suggestion = state.suggestion,
            availableMin = state.availableMin,
            isWeatherAvailable = state.isWeatherAvailable,
            onActivityClick = { id -> onActivityClick(id, true) }
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "OR EXPLORE ALL ACTIVITIES",
                style = MaterialTheme.typography.labelSmall,
                color = ColorPalette.Neutral.t700
            )
            CategoryChips(
                selectedCategory = state.selectedCategory,
                onCategorySelected = onCategorySelected
            )
            for (activity in state.activities) {
                ActivityCard(
                    activity = activity,
                    onClick = { onActivityClick(activity.id, false) }
                )
            }
        }
    }
}


@Composable
private fun PlaceSelector(
    selectedPlace: PlaceLabel?,
    onPlaceSelected: (PlaceLabel?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Where are you?",
                style = MaterialTheme.typography.labelMedium,
                color = ColorPalette.Neutral.t850
            )
            for (place in PlaceLabel.entries) {
                BreakLoopChip(
                    text = place.name.lowercase().replaceFirstChar { it.uppercase() },
                    selected = selectedPlace == place,
                    onClick = { onPlaceSelected(if (selectedPlace == place) null else place) },
                    modifier = Modifier,
                    selectedColor = ColorPalette.Fern.t500
                )
            }
        }
        Text(
            text = "Used anonymously to improve suggestions.",
            style = MaterialTheme.typography.bodySmall,
            color = ColorPalette.Neutral.t700
        )
    }
}

@Preview(showBackground = true, name = "Place selected")
@Composable
private fun PlaceSelectorSelectedPreview() {
    BreakLoopTheme {
        PlaceSelector(
            selectedPlace = PlaceLabel.HOME,
            onPlaceSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "No place selected")
@Composable
private fun PlaceSelectorEmptyPreview() {
    BreakLoopTheme {
        PlaceSelector(
            selectedPlace = null,
            onPlaceSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
