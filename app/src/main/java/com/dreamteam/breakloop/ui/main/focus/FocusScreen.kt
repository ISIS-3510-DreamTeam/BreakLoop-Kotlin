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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

@Composable
fun FocusScreen(
    modifier: Modifier = Modifier,
    viewModel: FocusSessionViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FocusContent(
        state = state,
        onDurationChange = viewModel::onDurationChange,
        onFocusGoalChange = viewModel::onFocusGoalChange,
        onSoundscapeChange = viewModel::onSoundscapeChange,
        onAppShieldChange = viewModel::onAppShieldChange,
        onStartFocus = viewModel::startFocus,
        modifier = modifier
    )
}

@Composable
private fun FocusContent(
    state: FocusSessionUiState,
    onDurationChange: (Int) -> Unit,
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

        FocusHeader()

        Spacer(modifier = Modifier.height(4.dp))

        BreakLoopCard(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {

            FocusIcon(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ready to Focus?",
                style = Typography.headlineMedium,
                color = ColorPalette.Neutral.t1000,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Select your duration and choose your commitment.",
                style = Typography.bodyMedium,
                color = ColorPalette.Neutral.t850,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "SESSION LENGTH",
            style = Typography.labelLarge,
            color = ColorPalette.Neutral.t850,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DurationButton(
                text = "25m",
                selected = state.selectedDuration == 25,
                onClick = { onDurationChange(25) },
                modifier = Modifier.weight(1f)
            )

            DurationButton(
                text = "45m",
                selected = state.selectedDuration == 45,
                onClick = { onDurationChange(45) },
                modifier = Modifier.weight(1f)
            )

            DurationButton(
                text = "60m",
                selected = state.selectedDuration == 60,
                onClick = { onDurationChange(60) },
                modifier = Modifier.weight(1f)
            )

            DurationButton(
                text = "Custom",
                selected = state.selectedDuration !in listOf(25, 45, 60),
                onClick = { onDurationChange(state.customDuration) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        BreakLoopCard(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "FOCUS GOAL (OPTIONAL)",
                style = Typography.labelLarge,
                color = ColorPalette.Neutral.t850
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = state.focusGoal,
                onValueChange = onFocusGoalChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("What do you want to focus on?")
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ColorPalette.Neutral.Snow,
                    unfocusedContainerColor = ColorPalette.Neutral.Snow
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        BreakLoopCard(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "COZY SOUNDSCAPE",
                style = Typography.labelLarge,
                color = ColorPalette.Neutral.t850
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SoundscapeButton(
                    text = "🔥 Hearth\nCrackle",
                    selected = state.selectedSoundscape == Soundscape.HEARTH,
                    onClick = {
                        onSoundscapeChange(Soundscape.HEARTH)
                    },
                    modifier = Modifier.weight(1f)
                )

                SoundscapeButton(
                    text = "🌧️ Soft Rain",
                    selected = state.selectedSoundscape == Soundscape.RAIN,
                    onClick = {
                        onSoundscapeChange(Soundscape.RAIN)
                    },
                    modifier = Modifier.weight(1f)
                )

                SoundscapeButton(
                    text = "🤫 Pure Silence",
                    selected = state.selectedSoundscape == Soundscape.SILENCE,
                    onClick = {
                        onSoundscapeChange(Soundscape.SILENCE)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                    checked = state.appShieldEnabled,
                    onCheckedChange = onAppShieldChange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BreakLoopButton(
            text = "⌛ START FOCUS",
            onClick = onStartFocus,
            loading = state.isLoading,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FocusHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(R.drawable.breakloop_logo),
                contentDescription = "BreakLoop logo",
                modifier = Modifier.size(105.dp)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Column {
                Text(
                    text = "BREAKLOOP",
                    style = Typography.headlineSmall,
                    color = ColorPalette.SpicyPaprika.t500
                )

                Text(
                    text = "FOCUS SETUP",
                    style = Typography.titleMedium,
                    color = ColorPalette.Neutral.t850
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeaderBadge(
                text = "🛡️ Shield Test"
            )

            HeaderBadge(
                text = "🔥 18d"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(ColorPalette.Neutral.t1000)
        )
    }
}

@Composable
private fun HeaderBadge(
    text: String
) {
    Box(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = ColorPalette.Neutral.t1000,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 5.dp
            )
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
            color = ColorPalette.Neutral.t1000,
            textAlign = TextAlign.Center
        )
    }
}

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
                    ColorPalette.Neutral.t1000,
                    shape
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    ColorPalette.Neutral.Snow,
                    shape
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
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
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
                focusGoal = "Study Economics Reading",
                selectedSoundscape = Soundscape.HEARTH,
                appShieldEnabled = true,
                isLoading = false,
                error = null
            ),
            onDurationChange = {},
            onFocusGoalChange = {},
            onSoundscapeChange = {},
            onAppShieldChange = {},
            onStartFocus = {}
        )
    }
}