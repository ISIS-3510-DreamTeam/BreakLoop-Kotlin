package com.dreamteam.breakloop.ui.main.stats.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    supportingColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    BreakLoopCard(modifier = modifier) {
        Text(
            text = label,
            style = Typography.bodyMedium,
            color = ColorPalette.Neutral.t700
        )
        val valueColor = if (value == "—") ColorPalette.Neutral.t500 else ColorPalette.Neutral.t1000
        Text(
            text = value,
            style = Typography.displaySmall,
            color = valueColor
        )
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = Typography.bodySmall,
                color = supportingColor
            )
        }
    }
}
