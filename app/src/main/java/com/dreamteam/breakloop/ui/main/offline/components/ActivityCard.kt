package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopChip
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun ActivityCard(
    activity: OfflineActivity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BreakLoopCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = categoryColor(activity.category),
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activity.category.name.first().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = ColorPalette.Neutral.t1000
                )
            }

            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${activity.durationMin} MIN",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                color= ColorPalette.GoldenOrange.t100,
                                shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text(
                        text = "+${activity.xp} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorPalette.Fern.t500,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorPalette.Neutral.t1000
                )
            }
        }

        // Descripción
        Text(
            text = activity.prompt,
            style = MaterialTheme.typography.bodySmall,
            color = ColorPalette.Neutral.t700
        )

        // Pie: categoría + botón
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activity.category.name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = ColorPalette.Neutral.t700
            )
            BreakLoopChip(
                text = "Do this now ▶",
                selected= true,
                onClick = onClick,
                modifier = modifier
            )
        }
    }
}

fun categoryColor(category: ActivityCategory): Color = when (category) {
    ActivityCategory.MOVE -> ColorPalette.Fern.t100
    ActivityCategory.CREATE -> ColorPalette.SpicyPaprika.t100
    ActivityCategory.RELAX -> ColorPalette.GoldenOrange.t100
    ActivityCategory.SOCIALIZE -> ColorPalette.Neutral.t100
}