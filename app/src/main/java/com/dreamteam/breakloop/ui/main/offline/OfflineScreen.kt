package com.dreamteam.breakloop.ui.main.offline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    onActivityClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        OfflineUiState.Loading -> LoadingContent(modifier = modifier)
        is OfflineUiState.Content -> OfflineContent(
            state,
            viewModel::onAvailableTimeSelected,
            viewModel::onCategorySelected,
            onActivityClick = onActivityClick
        )

    }
}

@Composable
fun OfflineContent(
    state: OfflineUiState.Content,
    onAvailableTimeSelected: (Int) -> Unit,
    onCategorySelected: (ActivityCategory?) -> Unit,
    onActivityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
            .background(Snow)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BreakLoopCard(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text="What will you do offline?",
                style = MaterialTheme.typography.headlineMedium,
                color = ColorPalette.Neutral.t1000
            )
        }
        SuggestedCard(
            suggestion = state.suggestion,
            availableMin = state.availableMin,
            isWeatherAvailable = state.isWeatherAvailable,
            onTimeSelected = onAvailableTimeSelected,
            onActivityClick = onActivityClick
        )
        CategoryChips(
            selectedCategory = state.selectedCategory,
            onCategorySelected = onCategorySelected
        )
        for (activity in state.activities){
            ActivityCard(
                activity = activity,
                onClick = { onActivityClick(activity.id) }
            )

        }

    }
}