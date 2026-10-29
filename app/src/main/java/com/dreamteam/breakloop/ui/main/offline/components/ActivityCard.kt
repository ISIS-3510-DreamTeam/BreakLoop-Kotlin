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
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.ui.components.BreakLoopCard
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
            .clickable { onClick() },
        elevated = false
    ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(categoryColor(activity.category), RoundedCornerShape(8.dp))
                .border(1.5.dp, ColorPalette.Neutral.t1000, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon(activity.category),
                contentDescription = activity.category.name,
                tint = ColorPalette.Neutral.t1000,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
                color = ColorPalette.Neutral.t1000
            )
            Text(
                text = "${activity.durationMin} MIN · +${activity.xp} XP · ${activity.category.name}",
                style = MaterialTheme.typography.labelSmall,
                color = ColorPalette.Neutral.t700
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = ColorPalette.Neutral.t700
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

fun categoryIcon(category: ActivityCategory): ImageVector = when (category) {
    ActivityCategory.MOVE -> Icons.Outlined.DirectionsRun
    ActivityCategory.CREATE -> Icons.Outlined.Brush
    ActivityCategory.RELAX -> Icons.Outlined.SelfImprovement
    ActivityCategory.SOCIALIZE -> Icons.Outlined.Groups
}

fun rewardIcon(category: ActivityCategory): ImageVector = when (category) {
    ActivityCategory.MOVE -> Icons.Outlined.Eco
    ActivityCategory.CREATE -> Icons.Outlined.Whatshot
    ActivityCategory.RELAX -> Icons.Outlined.Spa
    ActivityCategory.SOCIALIZE -> Icons.Outlined.Favorite
}