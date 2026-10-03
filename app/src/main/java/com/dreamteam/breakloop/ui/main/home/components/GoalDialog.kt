package com.dreamteam.breakloop.ui.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopTextField
import com.dreamteam.breakloop.ui.components.MonoTitle

@Composable
fun GoalDialog(
    initialGoalMs: Long?,
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