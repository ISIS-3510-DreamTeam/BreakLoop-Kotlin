package com.dreamteam.breakloop.ui.main.focus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.local.repository.FocusSessionRepository
import com.dreamteam.breakloop.remote.RetrofitInstance
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

// -----------------------------------------------------------------------------
// Main Screen
// -----------------------------------------------------------------------------

@Composable
fun FocusScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val database = remember {
        AppDatabase.getInstance(context)
    }

    val repository = remember {
        FocusSessionRepository(
            focusSessionDao = database.focusSessionDao(),
            focusApi = RetrofitInstance.focusApi
        )
    }

    val factory = remember {
        FocusViewModelFactory(
            focusSessionRepository = repository,
            context = context
        )
    }

    val viewModel: FocusViewModel = viewModel(
        factory = factory
    )

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FocusContent(
        state = state,
        onDurationChange = viewModel::onDurationChange,
        onCustomDurationSelected = viewModel::onCustomDurationSelected,
        onCustomDurationChange = viewModel::onCustomDurationChange,
        onFocusGoalChange = viewModel::onFocusGoalChange,
        onSoundscapeChange = viewModel::onSoundscapeChange,
        onAppShieldChange = viewModel::onAppShieldChange,
        onStartFocus = viewModel::startFocus,
        onStopFocus = viewModel::stopFocus,
        modifier = modifier
    )
}

// -----------------------------------------------------------------------------
// Content
// -----------------------------------------------------------------------------

@Composable
private fun FocusContent(
    state: FocusSessionUiState,
    onDurationChange: (Int) -> Unit,
    onCustomDurationSelected: () -> Unit,
    onCustomDurationChange: (Int) -> Unit,
    onFocusGoalChange: (String) -> Unit,
    onSoundscapeChange: (Soundscape) -> Unit,
    onAppShieldChange: (Boolean) -> Unit,
    onStartFocus: () -> Unit,
    onStopFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isSessionActive) {
        ActiveFocusContent(
            state = state,
            onStopFocus = onStopFocus,
            modifier = modifier
        )
    } else {
        FocusSetupScreen(
            state = state,
            onDurationChange = onDurationChange,
            onCustomDurationSelected = onCustomDurationSelected,
            onCustomDurationChange = onCustomDurationChange,
            onFocusGoalChange = onFocusGoalChange,
            onSoundscapeChange = onSoundscapeChange,
            onAppShieldChange = onAppShieldChange,
            onStartFocus = onStartFocus,
            modifier = modifier
        )
    }
}

// -----------------------------------------------------------------------------
// Focus Setup
// -----------------------------------------------------------------------------

@Composable
private fun FocusSetupScreen(
    state: FocusSessionUiState,
    onDurationChange: (Int) -> Unit,
    onCustomDurationSelected: () -> Unit,
    onCustomDurationChange: (Int) -> Unit,
    onFocusGoalChange: (String) -> Unit,
    onSoundscapeChange: (Soundscape) -> Unit,
    onAppShieldChange: (Boolean) -> Unit,
    onStartFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FocusSetupContent(
            state = state,
            onDurationChange = onDurationChange,
            onCustomDurationSelected = onCustomDurationSelected,
            onCustomDurationChange = onCustomDurationChange,
            onFocusGoalChange = onFocusGoalChange,
            onSoundscapeChange = onSoundscapeChange,
            onAppShieldChange = onAppShieldChange,
            onStartFocus = onStartFocus
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
private fun FocusSetupContent(
    state: FocusSessionUiState,
    onDurationChange: (Int) -> Unit,
    onCustomDurationSelected: () -> Unit,
    onCustomDurationChange: (Int) -> Unit,
    onFocusGoalChange: (String) -> Unit,
    onSoundscapeChange: (Soundscape) -> Unit,
    onAppShieldChange: (Boolean) -> Unit,
    onStartFocus: () -> Unit
) {
    FocusIntroCard()

    Spacer(
        modifier = Modifier.height(16.dp)
    )

    DurationSelector(
        selectedDuration = state.selectedDuration,
        customDuration = state.customDuration,
        isCustomSelected = state.isCustomDurationSelected,
        onDurationChange = onDurationChange,
        onCustomDurationSelected = onCustomDurationSelected,
        onCustomDurationChange = onCustomDurationChange
    )

    Spacer(
        modifier = Modifier.height(20.dp)
    )

    FocusGoalCard(
        focusGoal = state.focusGoal,
        onFocusGoalChange = onFocusGoalChange
    )

    Spacer(
        modifier = Modifier.height(20.dp)
    )

    SoundscapeCard(
        selectedSoundscape = state.selectedSoundscape,
        onSoundscapeChange = onSoundscapeChange
    )

    Spacer(
        modifier = Modifier.height(20.dp)
    )

    AppShieldCard(
        enabled = state.appShieldEnabled,
        onEnabledChange = onAppShieldChange
    )

    Spacer(
        modifier = Modifier.height(24.dp)
    )

    state.error?.let { error ->
        Text(
            text = error,
            style = Typography.bodyMedium,
            color = ColorPalette.SpicyPaprika.t500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )
    }

    BreakLoopButton(
        text = "⌛ START FOCUS",
        onClick = onStartFocus,
        loading = state.isLoading,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

// -----------------------------------------------------------------------------
// Setup Cards
// -----------------------------------------------------------------------------

@Composable
private fun FocusIntroCard() {
    BreakLoopCard(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        FocusIcon(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Ready to Focus?",
            style = Typography.headlineMedium,
            color = ColorPalette.Neutral.t1000,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "Select your duration and choose your commitment.",
            style = Typography.bodyMedium,
            color = ColorPalette.Neutral.t850,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// -----------------------------------------------------------------------------
// Duration Selector
// -----------------------------------------------------------------------------

@Composable
private fun DurationSelector(
    selectedDuration: Int,
    customDuration: Int,
    isCustomSelected: Boolean,
    onDurationChange: (Int) -> Unit,
    onCustomDurationSelected: () -> Unit,
    onCustomDurationChange: (Int) -> Unit
) {
    Text(
        text = "SESSION LENGTH",
        style = Typography.labelLarge,
        color = ColorPalette.Neutral.t850,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DurationButton(
            text = "25m",
            selected = selectedDuration == 25 && !isCustomSelected,
            onClick = {
                onDurationChange(25)
            },
            modifier = Modifier.weight(1f)
        )

        DurationButton(
            text = "45m",
            selected = selectedDuration == 45 && !isCustomSelected,
            onClick = {
                onDurationChange(45)
            },
            modifier = Modifier.weight(1f)
        )

        DurationButton(
            text = "60m",
            selected = selectedDuration == 60 && !isCustomSelected,
            onClick = {
                onDurationChange(60)
            },
            modifier = Modifier.weight(1f)
        )

        DurationButton(
            text = "Custom",
            selected = isCustomSelected,
            onClick = {
                onCustomDurationSelected()
            },
            modifier = Modifier.weight(1f)
        )
    }

    if (isCustomSelected) {
        Spacer(
            modifier = Modifier.height(12.dp)
        )

        TextField(
            value = customDuration.toString(),
            onValueChange = { value ->
                val duration = value
                    .filter { it.isDigit() }
                    .toIntOrNull()

                if (duration != null && duration > 0) {
                    onCustomDurationChange(duration)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            label = {
                Text(
                    text = "Custom duration"
                )
            },
            placeholder = {
                Text(
                    text = "Enter minutes"
                )
            },
            suffix = {
                Text(
                    text = "min"
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ColorPalette.Neutral.Snow,
                unfocusedContainerColor = ColorPalette.Neutral.Snow,
                focusedTextColor = ColorPalette.Neutral.t1000,
                unfocusedTextColor = ColorPalette.Neutral.t1000,
                focusedLabelColor = ColorPalette.SpicyPaprika.t500,
                unfocusedLabelColor = ColorPalette.Neutral.t700,
                focusedPlaceholderColor = ColorPalette.Neutral.t700,
                unfocusedPlaceholderColor = ColorPalette.Neutral.t700,
                focusedIndicatorColor = ColorPalette.SpicyPaprika.t500,
                unfocusedIndicatorColor = ColorPalette.Neutral.t500,
                cursorColor = ColorPalette.SpicyPaprika.t500
            )
        )
    }
}

// -----------------------------------------------------------------------------
// Focus Goal
// -----------------------------------------------------------------------------

@Composable
private fun FocusGoalCard(
    focusGoal: String,
    onFocusGoalChange: (String) -> Unit
) {
    BreakLoopCard(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "FOCUS GOAL (OPTIONAL)",
            style = Typography.labelLarge,
            color = ColorPalette.Neutral.t850
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        TextField(
            value = focusGoal,
            onValueChange = onFocusGoalChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = {
                Text(
                    text = "What do you want to focus on?"
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ColorPalette.Neutral.Snow,
                unfocusedContainerColor = ColorPalette.Neutral.Snow,
                focusedTextColor = ColorPalette.Neutral.t1000,
                unfocusedTextColor = ColorPalette.Neutral.t1000,
                focusedPlaceholderColor = ColorPalette.Neutral.t700,
                unfocusedPlaceholderColor = ColorPalette.Neutral.t700,
                focusedIndicatorColor = ColorPalette.SpicyPaprika.t500,
                unfocusedIndicatorColor = ColorPalette.Neutral.t500,
                cursorColor = ColorPalette.SpicyPaprika.t500
            )
        )
    }
}

// -----------------------------------------------------------------------------
// Soundscape
// -----------------------------------------------------------------------------

@Composable
private fun SoundscapeCard(
    selectedSoundscape: Soundscape,
    onSoundscapeChange: (Soundscape) -> Unit
) {
    BreakLoopCard(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "COZY SOUNDSCAPE",
            style = Typography.labelLarge,
            color = ColorPalette.Neutral.t850
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SoundscapeButton(
                text = "🔥 Hearth\nCrackle",
                selected = selectedSoundscape == Soundscape.HEARTH,
                onClick = {
                    onSoundscapeChange(Soundscape.HEARTH)
                },
                modifier = Modifier.weight(1f)
            )

            SoundscapeButton(
                text = "🌧️ Soft Rain",
                selected = selectedSoundscape == Soundscape.RAIN,
                onClick = {
                    onSoundscapeChange(Soundscape.RAIN)
                },
                modifier = Modifier.weight(1f)
            )

            SoundscapeButton(
                text = "🤫 Pure Silence",
                selected = selectedSoundscape == Soundscape.SILENCE,
                onClick = {
                    onSoundscapeChange(Soundscape.SILENCE)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// App Shield
// -----------------------------------------------------------------------------

@Composable
private fun AppShieldCard(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    BreakLoopCard(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "🛡️ App Shield Active",
                    style = Typography.titleMedium,
                    color = ColorPalette.Neutral.t1000
                )

                Text(
                    text = "10-second breathing challenge on blocked apps",
                    style = Typography.bodySmall,
                    color = ColorPalette.Neutral.t700
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ColorPalette.Neutral.Snow,
                    checkedTrackColor = ColorPalette.SpicyPaprika.t500,
                    checkedBorderColor = ColorPalette.SpicyPaprika.t500,
                    uncheckedThumbColor = ColorPalette.Neutral.Snow,
                    uncheckedTrackColor = ColorPalette.Neutral.t500,
                    uncheckedBorderColor = ColorPalette.Neutral.t700
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Active Focus Session
// -----------------------------------------------------------------------------

@Composable
private fun ActiveFocusContent(
    state: FocusSessionUiState,
    onStopFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = state.remainingSeconds / 60
    val seconds = state.remainingSeconds % 60

    val timeText = String.format(
        "%02d:%02d",
        minutes,
        seconds
    )

    val totalSeconds = state.selectedDuration * 60

    val progress = if (totalSeconds > 0) {
        state.elapsedSeconds.toFloat() / totalSeconds.toFloat()
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ActiveFocusTopBar()

        Spacer(
            modifier = Modifier.height(45.dp)
        )

        FocusSprintPet()

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = timeText,
            style = Typography.displayLarge,
            color = ColorPalette.Neutral.t1000,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        if (state.focusGoal.isNotBlank()) {
            FocusGoalBadge(
                focusGoal = state.focusGoal
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        FocusProgressBar(
            progress = progress
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        BreakLoopButton(
            text = "END SESSION EARLY",
            onClick = onStopFocus,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Tap to finish and log earned ${
                getSoundscapeName(state.selectedSoundscape)
            } vitality",
            style = Typography.bodySmall,
            color = ColorPalette.Neutral.t700,
            textAlign = TextAlign.Center
        )
    }
}

// -----------------------------------------------------------------------------
// Active Focus Top Bar
// -----------------------------------------------------------------------------

@Composable
private fun ActiveFocusTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ColorPalette.Fern.t100)
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = "FOCUS SPRINT",
                style = Typography.titleMedium,
                color = ColorPalette.Neutral.t850
            )
        }

        Box(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = ColorPalette.Neutral.t1000,
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 7.dp
                )
        ) {
            Text(
                text = "🛡 DO NOT DISTURB",
                style = Typography.labelLarge,
                color = ColorPalette.Neutral.t850
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Focus Goal
// -----------------------------------------------------------------------------

@Composable
private fun FocusGoalBadge(
    focusGoal: String
) {
    Box(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = ColorPalette.Neutral.t850,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 7.dp
            )
    ) {
        Text(
            text = "Target: $focusGoal",
            style = Typography.labelMedium,
            color = ColorPalette.Neutral.t850,
            textAlign = TextAlign.Center
        )
    }
}

// -----------------------------------------------------------------------------
// Progress Bar
// -----------------------------------------------------------------------------

@Composable
private fun FocusProgressBar(
    progress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .height(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 2.dp,
                color = ColorPalette.Neutral.t850,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(
                    progress.coerceIn(0f, 1f)
                )
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(ColorPalette.SpicyPaprika.t500)
        )
    }
}

// -----------------------------------------------------------------------------
// Focus Icon
// -----------------------------------------------------------------------------

@Composable
private fun FocusIcon(
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)

    Box(
        modifier = modifier.size(40.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(
                    x = 4.dp,
                    y = 4.dp
                )
                .background(
                    color = ColorPalette.Neutral.t1000,
                    shape = shape
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = ColorPalette.Neutral.Snow,
                    shape = shape
                )
                .border(
                    width = 2.dp,
                    color = ColorPalette.Neutral.t1000,
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    R.drawable.ic_nav_focus_selected
                ),
                contentDescription = "Focus",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Duration Button
// -----------------------------------------------------------------------------

@Composable
private fun DurationButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) {
                    ColorPalette.SpicyPaprika.t500
                } else {
                    ColorPalette.Neutral.Snow
                }
            )
            .border(
                width = 2.dp,
                color = ColorPalette.Neutral.t1000,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
            color = if (selected) {
                ColorPalette.Neutral.Snow
            } else {
                ColorPalette.Neutral.t1000
            },
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

// -----------------------------------------------------------------------------
// Soundscape Button
// -----------------------------------------------------------------------------

@Composable
private fun SoundscapeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) {
                ColorPalette.SpicyPaprika.t100
            } else {
                ColorPalette.Neutral.Snow
            },
            contentColor = ColorPalette.Neutral.t1000
        )
    ) {
        Text(
            text = text,
            style = Typography.labelMedium
        )
    }
}

// -----------------------------------------------------------------------------
// Focus Sprint Pet
// -----------------------------------------------------------------------------

@Composable
private fun FocusSprintPet() {
    Box(
        modifier = Modifier
            .size(
                width = 190.dp,
                height = 195.dp
            )
            .clip(
                RoundedCornerShape(34.dp)
            )
            .background(
                ColorPalette.Neutral.Snow
            )
            .border(
                width = 5.dp,
                color = ColorPalette.Neutral.t1000,
                shape = RoundedCornerShape(34.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(
                    R.drawable.ic_nav_focus_selected
                ),
                contentDescription = "Focus",
                modifier = Modifier.size(64.dp)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "• ᴗ •",
                style = Typography.titleLarge,
                color = ColorPalette.Neutral.t1000
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "MINDFUL FOCUS",
                style = Typography.labelMedium,
                color = ColorPalette.SpicyPaprika.t500,
                textAlign = TextAlign.Center
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Helper Functions
// -----------------------------------------------------------------------------

private fun getSoundscapeName(
    soundscape: Soundscape
): String {
    return when (soundscape) {
        Soundscape.HEARTH -> "Hearth"
        Soundscape.RAIN -> "Rain"
        Soundscape.SILENCE -> "Silence"
    }
}

// -----------------------------------------------------------------------------
// Setup Preview
// -----------------------------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun FocusScreenPreview() {
    BreakLoopTheme {
        FocusContent(
            state = FocusSessionUiState(
                selectedDuration = 25,
                customDuration = 25,
                isCustomDurationSelected = false,
                focusGoal = "Study Economics Reading",
                selectedSoundscape = Soundscape.HEARTH,
                appShieldEnabled = true,
                isSessionActive = false,
                isPaused = false,
                remainingSeconds = 0,
                elapsedSeconds = 0,
                isLoading = false,
                error = null
            ),
            onDurationChange = {},
            onCustomDurationSelected = {},
            onCustomDurationChange = {},
            onFocusGoalChange = {},
            onSoundscapeChange = {},
            onAppShieldChange = {},
            onStartFocus = {},
            onStopFocus = {}
        )
    }
}

// -----------------------------------------------------------------------------
// Active Focus Preview
// -----------------------------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ActiveFocusPreview() {
    BreakLoopTheme {
        ActiveFocusContent(
            state = FocusSessionUiState(
                selectedDuration = 25,
                customDuration = 25,
                isCustomDurationSelected = false,
                focusGoal = "Study Economics Reading",
                selectedSoundscape = Soundscape.HEARTH,
                appShieldEnabled = true,
                isSessionActive = true,
                isPaused = false,
                remainingSeconds = 24 * 60 + 30,
                elapsedSeconds = 30,
                isLoading = false,
                error = null
            ),
            onStopFocus = {}
        )
    }
}