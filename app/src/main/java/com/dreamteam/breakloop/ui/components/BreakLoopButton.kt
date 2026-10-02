package com.dreamteam.breakloop.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun BreakLoopButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val paprika = ColorPalette.SpicyPaprika.t500
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        shape = RoundedCornerShape(50),   // round
        colors = ButtonDefaults.buttonColors(containerColor = ColorPalette.SpicyPaprika.t500, disabledContainerColor = paprika.copy(alpha = 0.6f)),
        border = BorderStroke(2.dp, ColorPalette.Neutral.t1000),
        modifier = modifier.fillMaxWidth(),
    ) {
        if (loading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text = text.uppercase(), style = MonoLabel, color = ColorPalette.Neutral.Snow)
        }
    }
}