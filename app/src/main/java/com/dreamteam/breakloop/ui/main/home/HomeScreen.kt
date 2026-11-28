package com.dreamteam.breakloop.ui.main.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.components.OfflineNotice
import com.dreamteam.breakloop.ui.main.achievements.AchievementsViewModel
import com.dreamteam.breakloop.ui.main.achievements.components.AchievementsCard
import com.dreamteam.breakloop.ui.main.achievements.components.CelebrationDialog
import com.dreamteam.breakloop.ui.main.home.components.GoalDialog
import com.dreamteam.breakloop.ui.main.home.components.ScreenTimeGoalCard
import com.dreamteam.breakloop.ui.main.home.pet.PetCard
import com.dreamteam.breakloop.ui.main.home.pet.PetPickerDialog
import com.dreamteam.breakloop.ui.main.home.pet.PetViewModel
import com.dreamteam.breakloop.ui.main.stats.NoPermissionContent

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAchievementsClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
    petViewModel: PetViewModel = viewModel(),
    achievementsViewModel: AchievementsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val petState by petViewModel.uiState.collectAsStateWithLifecycle()
    var showGoalDialog by rememberSaveable { mutableStateOf(false) }
    var showPetPicker by rememberSaveable { mutableStateOf(false) }
    val forecast by viewModel.forecast.collectAsStateWithLifecycle()
    val achievementsState by achievementsViewModel.uiState.collectAsStateWithLifecycle()
    val celebration by achievementsViewModel.celebration.collectAsStateWithLifecycle()
    val connectivity by achievementsViewModel.connectivityStatus.collectAsStateWithLifecycle()
    val askNotificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    // Re-evaluates every time Home is shown again (for example, coming back from the achievements grid),
    // so the card never shows an old count
    LifecycleResumeEffect(Unit) {
        achievementsViewModel.load()
        onPauseOrDispose { }
    }

    Column(modifier = modifier.fillMaxSize()) {

        OfflineNotice(
            status = connectivity,
            offlineMessage = "No internet connection. Your screen time, streak and achievements keep counting on this phone.",
        )

        // No scroll: the three cards fit on the screen
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PetCard(
                state = petState,
                onChangePet = {
                    petViewModel.loadCatalog()
                    showPetPicker = true
                },
            )

            AchievementsCard(
                state = achievementsState,
                onClick = onAchievementsClick,
            )

            when (val current = state) {
                is HomeUiState.Loading -> LoadingContent(Modifier.weight(1f))
                is HomeUiState.NoPermission -> NoPermissionContent(Modifier.weight(1f))
                is HomeUiState.Content -> {
                    ScreenTimeGoalCard(
                        usedMs = current.usedMs,
                        progress = current.progress,
                        onEditGoal = {
                            viewModel.loadStreakSimulation()
                            showGoalDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (showGoalDialog) {
                        GoalDialog(
                            initialGoalMs = current.goalMs,
                            forecast = forecast,
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
    }

    celebration?.let {
        CelebrationDialog(progress = it, onDismiss = achievementsViewModel::dismissCelebration)
    }

    if (showPetPicker) {
        PetPickerDialog(
            pets = petState.catalog,
            onPick = { petId ->
                petViewModel.changePet(petId)
                showPetPicker = false
            },
            onDismiss = { showPetPicker = false },
        )
    }
}