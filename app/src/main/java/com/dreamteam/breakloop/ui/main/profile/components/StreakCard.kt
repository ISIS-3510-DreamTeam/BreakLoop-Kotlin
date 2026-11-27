package com.dreamteam.breakloop.ui.main.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun StreakCard(streakDays: Int, modifier: Modifier = Modifier) {
    BreakLoopCard(modifier = modifier, elevated = false, contentPadding = 12.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "STREAK",
                style = MaterialTheme.typography.labelSmall,
                color = ColorPalette.Neutral.t700,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // same icon as the top bar
                Icon(
                    painter = painterResource(R.drawable.ic_streak),
                    contentDescription = null,
                    tint = ColorPalette.Neutral.t1000,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = "$streakDays",
                    style = MaterialTheme.typography.headlineSmall,
                    color = ColorPalette.Neutral.t1000,
                )
            }
            Text(
                text = if (streakDays == 1) "day" else "days",
                style = MaterialTheme.typography.bodySmall,
                color = ColorPalette.Neutral.t700,
            )
        }
    }
}
