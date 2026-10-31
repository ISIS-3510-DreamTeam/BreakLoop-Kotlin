package com.dreamteam.breakloop.ui.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopTextField
import com.dreamteam.breakloop.ui.main.home.StreakForecastState
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoTitle
import com.dreamteam.breakloop.ui.util.formatDuration
import kotlin.math.roundToInt

@Composable
fun GoalDialog(
    initialGoalMs: Long?,
    forecast: StreakForecastState,
    onSave: (hours: Int, minutes: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var hours by rememberSaveable {
        mutableStateOf(initialGoalMs?.let { (it / 3_600_000).toString() } ?: "")
    }
    var minutes by rememberSaveable {
        mutableStateOf(initialGoalMs?.let { ((it % 3_600_000) / 60_000).toString() } ?: "")
    }

    val isValid = (0<=hours.toIntOrNull() ?: 0 && hours.toIntOrNull() ?: 0 <=23 && 0<=minutes.toIntOrNull() ?: 0 && minutes.toIntOrNull() ?: 0 <=59 && (hours.toIntOrNull() ?: 0 !=0 || minutes.toIntOrNull() ?: 0 !=0))

    Dialog(onDismissRequest = onDismiss) {
        BreakLoopCard {
            Text(text = "DAILY GOAL", style = MonoTitle)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BreakLoopTextField(
                    value = hours,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) hours = it },
                    label = "Hours",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                BreakLoopTextField(
                    value = minutes,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) minutes = it },
                    label = "Minutes",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            Spacer(Modifier.height(12.dp))
            val selectedMinutes = (hours.toIntOrNull() ?: 0) * 60 + (minutes.toIntOrNull() ?: 0)
            StreakForecast(
                forecast = forecast,
                selectedMinutes = selectedMinutes,
                onUseSuggestion = { suggested ->
                    hours = (suggested / 60).toString()
                    minutes = (suggested % 60).toString()
                },
            )
            Spacer(Modifier.height(16.dp))
            BreakLoopButton(
                text = "Change",
                onClick = { onSave(hours.toIntOrNull() ?: 0, minutes.toIntOrNull() ?: 0) },
                enabled = isValid
            )
            TextButton(
                onClick = onDismiss
            ) {Text("Cancel") }
        }
    }
}

@Composable
private fun StreakForecast(
    forecast: StreakForecastState,
    selectedMinutes: Int,
    onUseSuggestion: (Int) -> Unit,
) {
    val result = forecast.result
    when {
        forecast.isLoading -> Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("Simulating your next 30 days…", style = MaterialTheme.typography.bodySmall)
        }

        result == null || !result.hasData ->
            Text("Not enough usage history yet to estimate your chances.", style = MaterialTheme.typography.bodySmall)

        else -> Column {
            val probability = result.probabilityFor(selectedMinutes)
            val color = if (probability >= 0.7) ColorPalette.Fern.t500 else ColorPalette.GoldenOrange.t500
            Text(
                text = "Chance of a 7-day streak: ${(probability * 100).roundToInt()}%",
                color = color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )

            val suggested = result.suggestedGoalMinutes
            if (suggested == null) {
                Text("No goal up to 12h reaches a 70% chance.", style = MaterialTheme.typography.bodySmall)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Suggested: ${formatDuration(suggested * 60_000L)} (≥ 70%)",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (suggested != selectedMinutes) {
                        TextButton(onClick = { onUseSuggestion(suggested) }) { Text("Use") }
                    }
                }
            }
        }
    }
}