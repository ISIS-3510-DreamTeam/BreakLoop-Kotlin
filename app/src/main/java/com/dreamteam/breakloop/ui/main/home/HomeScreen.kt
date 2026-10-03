package com.dreamteam.breakloop.ui.main.home


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.home.components.GoalDialog
import com.dreamteam.breakloop.ui.main.home.components.ScreenTimeGoalCard
import com.dreamteam.breakloop.ui.main.stats.NoPermissionContent

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showGoalDialog by rememberSaveable { mutableStateOf(false) }
    val askNotificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    when (val current = state) {
        is HomeUiState.Loading -> LoadingContent(modifier)
        is HomeUiState.NoPermission -> NoPermissionContent(modifier)
        is HomeUiState.Content -> {HomeContent(content = current, onEditGoal = {showGoalDialog = true}, modifier = modifier)
            if (showGoalDialog) {
                GoalDialog(
                    initialGoalMs = current.goalMs,
                    onSave = { hours, minutes ->
                        viewModel.saveGoal(hours, minutes)
                        showGoalDialog = false
                        if (Build.VERSION.SDK_INT >= 33) {
                            askNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    onDismiss = { showGoalDialog = false },
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    content: HomeUiState.Content,
    onEditGoal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        ScreenTimeGoalCard(content.usedMs, content.progress, onEditGoal)
    }
}