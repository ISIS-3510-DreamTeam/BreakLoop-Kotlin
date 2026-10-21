package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.Recommendation
import com.dreamteam.breakloop.domain.enums.ReasonTag
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel
import com.dreamteam.breakloop.ui.theme.Typography

private data class TimeOption(val minutes: Int, val label: String, val caption: String)

private val timeOptions = listOf(
    TimeOption(5, "5m", "Quick"),
    TimeOption(15, "15m", "Refresh"),
    TimeOption(30, "30m", "Deep"),
)

@Composable
fun TimeSelector(
    availableMin: Int,
    onTimeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (option in timeOptions) {
            val selected = availableMin == option.minutes
            val shape = RoundedCornerShape(12.dp)
            val textColor = if (selected) ColorPalette.Neutral.Snow else ColorPalette.Neutral.t1000
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(
                        if (selected) ColorPalette.SpicyPaprika.t500 else ColorPalette.Neutral.Snow,
                        shape
                    )
                    .border(2.dp, ColorPalette.Neutral.t1000, shape)
                    .clickable { onTimeSelected(option.minutes) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = option.label, style = MonoLabel, color = textColor)
                Text(
                    text = option.caption.uppercase(),
                    style = Typography.labelSmall,
                    color = textColor.copy(alpha = if (selected) 0.9f else 0.7f)
                )
            }
        }
    }
}

@Composable
fun SuggestedCard(
    suggestion: Recommendation?,
    availableMin: Int,
    isWeatherAvailable: Boolean,
    onActivityClick: (String) -> Unit
) {
    BreakLoopCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ColorPalette.GoldenOrange.t100
    ) {
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
                    text = "SUGGESTED FOR YOU",
                    style = Typography.labelSmall,
                    color = ColorPalette.SpicyPaprika.t700
                )
            }
            if (!isWeatherAvailable) {
                Text(
                    text = "OFFLINE-BASED",
                    style = Typography.labelSmall,
                    color = ColorPalette.Neutral.t700
                )
            }
        }

        if (suggestion == null) {
            Text(
                text = "Nothing fits in $availableMin min. Try more time.",
                style = Typography.bodyMedium,
                color = ColorPalette.Neutral.t700,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            return@BreakLoopCard
        }

        val activity = suggestion.activity
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(categoryColor(activity.category), RoundedCornerShape(12.dp))
                    .border(2.dp, ColorPalette.Neutral.t1000, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(activity.category),
                    contentDescription = activity.category.name,
                    tint = ColorPalette.Neutral.t1000,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = Typography.titleLarge,
                    color = ColorPalette.Neutral.t1000
                )
                Text(
                    text = reasonText(suggestion.reasons, availableMin),
                    style = Typography.bodySmall,
                    color = ColorPalette.Fern.t500
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 12.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetaItem(Icons.Outlined.Timer, "${activity.durationMin} MIN", ColorPalette.Neutral.t1000)
            MetaItem(Icons.Outlined.Bolt, "+${activity.xp} XP", ColorPalette.Fern.t500)
            Text(
                text = activity.category.name,
                style = Typography.labelSmall,
                color = ColorPalette.Neutral.t700
            )
        }

        BreakLoopButton(
            text = "Do this now",
            onClick = { onActivityClick(activity.id) }
        )
    }
}

@Composable
private fun MetaItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(text = text, style = Typography.labelSmall, color = color)
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
