package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.Recommendation
import com.dreamteam.breakloop.domain.enums.ReasonTag
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopChip
import com.dreamteam.breakloop.ui.theme.Typography
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun SuggestedCard(
    suggestion: Recommendation?,
    availableMin: Int,
    isWeatherAvailable: Boolean,
    onTimeSelected: (Int) -> Unit,
    onActivityClick: (String) -> Unit
) {
    BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = ColorPalette.SpicyPaprika.t700,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "SUGGESTED FOR RIGHT NOW",
                    style = Typography.labelSmall,
                    color = ColorPalette.SpicyPaprika.t700
                )
            }
            if (!isWeatherAvailable) {
                Text(
                    text = "OFFLINE-BASED",
                    style = Typography.labelSmall,
                    color = ColorPalette.Neutral.t500
                )
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BreakLoopChip(
                "5m quick",
                selected = availableMin == 5,
                onClick = { onTimeSelected(5) },
                modifier = Modifier
            )
            BreakLoopChip(
                "15m refresh",
                selected = availableMin == 15,
                onClick = { onTimeSelected(15) },
                modifier = Modifier
            )
            BreakLoopChip(
                "30m deep",
                selected = availableMin == 30,
                onClick = { onTimeSelected(30) },
                modifier = Modifier
            )
        }
        if (suggestion == null) {
            Text(
                text = "Nothing fits in $availableMin min. Try more time.",
                style = Typography.bodySmall,
                color = ColorPalette.Neutral.t700
            )
        } else {
            Text(
                text = suggestion.activity.title,
                style = Typography.headlineSmall,
                color = ColorPalette.Neutral.t1000
            )
            Text(
                text = reasonText(suggestion.reasons, availableMin),
                style = Typography.bodySmall,
                color = ColorPalette.Fern.t500
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null,
                    tint = ColorPalette.Neutral.t700,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${suggestion.activity.durationMin} MIN ·",
                    style = Typography.labelSmall,
                    color = ColorPalette.Neutral.t700
                )
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = null,
                    tint = ColorPalette.Fern.t500,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "+${suggestion.activity.xp} XP",
                    style = Typography.labelSmall,
                    color = ColorPalette.Fern.t500
                )
            }
            BreakLoopButton(
                text = "Do this now",
                onClick = { onActivityClick(suggestion.activity.id) }
            )
        }
    }
}

fun reasonText(reasons: List<ReasonTag>, availableMin: Int): String {
    if (reasons.isEmpty()) {
        return "A good break for right now"
    } else {
        return reasons.take(2).joinToString(" · ") { tag ->
            when (tag) {
                ReasonTag.GOOD_WEATHER -> "Good weather for it"
                ReasonTag.FITS_AVAILABLE_TIME -> "Fits your $availableMin min"
                ReasonTag.RIGHT_TIME_OF_DAY -> "Great for this time of day"
                ReasonTag.MATCHES_INTEREST -> "Matches your interests"
            }
        }
    }
}
