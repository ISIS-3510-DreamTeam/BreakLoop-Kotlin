package com.dreamteam.breakloop.ui.main.stats.flaggedapps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

private const val DefaultMinutes = 30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLimitSheet(
    row: FlaggedAppRow,
    onDismiss: () -> Unit,
    onSave: (hours: Int, minutes: Int, noLimit: Boolean) -> Unit
) {
    val currentLimit = row.flagged?.dailyLimitMin
    var noLimit by remember(row.app.packageName) { mutableStateOf(currentLimit == null) }
    var hours by remember(row.app.packageName) { mutableIntStateOf((currentLimit ?: DefaultMinutes) / 60) }
    var minutes by remember(row.app.packageName) { mutableIntStateOf((currentLimit ?: DefaultMinutes) % 60) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ColorPalette.Neutral.Snow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon(row.app.packageName, size = 44.dp, label = row.app.label)
                Column {
                    Text(
                        text = row.app.label,
                        style = Typography.titleMedium,
                        color = ColorPalette.Neutral.t1000
                    )
                    Text(
                        text = "Daily limit",
                        style = Typography.bodySmall,
                        color = ColorPalette.Neutral.t700
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No limit",
                    style = Typography.bodyLarge,
                    color = ColorPalette.Neutral.t1000
                )
                Switch(
                    checked = noLimit,
                    onCheckedChange = { noLimit = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ColorPalette.SpicyPaprika.t500,
                        checkedThumbColor = ColorPalette.Neutral.Snow,
                        uncheckedTrackColor = ColorPalette.Neutral.t100,
                        uncheckedThumbColor = ColorPalette.Neutral.t500,
                        uncheckedBorderColor = ColorPalette.Neutral.t200
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (noLimit) 0.35f else 1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelPicker(
                    range = 0..23,
                    initialValue = hours,
                    onValueChange = { hours = it },
                    enabled = !noLimit,
                    modifier = Modifier.width(88.dp)
                )
                Text(
                    text = "h",
                    style = Typography.titleMedium,
                    color = ColorPalette.Neutral.t700,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer24()
                WheelPicker(
                    range = 0..59,
                    initialValue = minutes,
                    onValueChange = { minutes = it },
                    enabled = !noLimit,
                    modifier = Modifier.width(88.dp)
                )
                Text(
                    text = "min",
                    style = Typography.titleMedium,
                    color = ColorPalette.Neutral.t700,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss, modifier = Modifier.height(48.dp)) {
                    Text(
                        text = "Cancel",
                        style = Typography.labelLarge,
                        color = ColorPalette.Neutral.t700
                    )
                }
                BreakLoopButton(
                    text = "Save",
                    onClick = { onSave(hours, minutes, noLimit) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun Spacer24() = androidx.compose.foundation.layout.Spacer(Modifier.width(16.dp))
