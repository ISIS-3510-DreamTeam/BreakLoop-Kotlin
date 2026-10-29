package com.dreamteam.breakloop.ui.main.stats.flaggedapps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.FlaggedApp
import com.dreamteam.breakloop.domain.InstalledApp
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

private const val MAX_VISIBLE_APPS = 5

@Composable
fun FlaggedAppsCard(
    flaggedRows: List<FlaggedAppRow>?,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmpty = flaggedRows != null && flaggedRows.isEmpty()
    BreakLoopCard(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isEmpty) Modifier.clickable(onClick = onConfigureClick) else Modifier)
    ) {
        when {
            flaggedRows == null -> FlaggedAppsSkeleton()
            flaggedRows.isEmpty() -> FlaggedAppsPlaceholder(onConfigureClick)
            else -> FlaggedAppsList(flaggedRows, onConfigureClick)
        }
    }
}

@Composable
private fun CardTitle() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LockReset,
            contentDescription = null,
            tint = ColorPalette.Neutral.t700,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "TAMED DISTRACTION LOOPS",
            style = Typography.labelSmall,
            color = ColorPalette.Neutral.t700
        )
    }
}

@Composable
private fun FlaggedAppsPlaceholder(onConfigureClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CardTitle()
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(ColorPalette.GoldenOrange.t100, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = ColorPalette.GoldenOrange.t500,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Set limits per app",
                    style = Typography.titleMedium,
                    color = ColorPalette.Neutral.t1000
                )
                Text(
                    text = "Choose the apps you want to use more intentionally.",
                    style = Typography.bodySmall,
                    color = ColorPalette.Neutral.t700
                )
            }
        }
        BreakLoopButton(text = "Set up", onClick = onConfigureClick)
    }
}

@Composable
private fun FlaggedAppsList(
    rows: List<FlaggedAppRow>,
    onConfigureClick: () -> Unit
) {
    val visible = rows.take(MAX_VISIBLE_APPS)
    val hidden = rows.size - visible.size

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardTitle()
        IconButton(
            onClick = onConfigureClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Configure distracting apps",
                tint = ColorPalette.Neutral.t1000,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    Column {
        visible.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon(
                    packageName = row.app.packageName,
                    size = 40.dp,
                    label = row.app.label
                )
                Text(
                    text = row.app.label,
                    style = Typography.titleSmall,
                    color = ColorPalette.Neutral.t1000,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatDailyLimit(row.flagged?.dailyLimitMin),
                    style = Typography.labelMedium,
                    color = if (row.flagged?.dailyLimitMin == null) ColorPalette.Neutral.t700
                    else ColorPalette.SpicyPaprika.t700
                )
            }
            if (index != visible.lastIndex) {
                HorizontalDivider(color = ColorPalette.Neutral.t100)
            }
        }
        if (hidden > 0) {
            Text(
                text = "+$hidden more",
                style = Typography.labelMedium,
                color = ColorPalette.Neutral.t700,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun FlaggedAppsSkeleton() {
    val bar = ColorPalette.Neutral.t100
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CardTitle()
        repeat(3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBlock(Modifier.size(40.dp), bar, 10.dp)
                SkeletonBlock(Modifier.weight(1f).height(14.dp), bar, 7.dp)
                SkeletonBlock(Modifier.width(48.dp).height(14.dp), bar, 7.dp)
            }
        }
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier, color: Color, radius: androidx.compose.ui.unit.Dp) {
    Box(modifier.background(color, RoundedCornerShape(radius)))
}

private fun fakeRows(count: Int) = List(count) { i ->
    FlaggedAppRow(
        app = InstalledApp("com.example.app$i", listOf("Instagram", "TikTok", "YouTube", "X", "Reddit", "Netflix", "Twitch")[i % 7]),
        flagged = FlaggedApp("com.example.app$i", listOf(null, 45, 90, 60)[i % 4], 0L)
    )
}

@Preview(showBackground = true)
@Composable
private fun FlaggedAppsCardEmptyPreview() =
    FlaggedAppsCard(flaggedRows = emptyList(), onConfigureClick = {}, modifier = Modifier.padding(16.dp))

@Preview(showBackground = true)
@Composable
private fun FlaggedAppsCardThreePreview() =
    FlaggedAppsCard(flaggedRows = fakeRows(3), onConfigureClick = {}, modifier = Modifier.padding(16.dp))

@Preview(showBackground = true)
@Composable
private fun FlaggedAppsCardSevenPreview() =
    FlaggedAppsCard(flaggedRows = fakeRows(7), onConfigureClick = {}, modifier = Modifier.padding(16.dp))

@Preview(showBackground = true)
@Composable
private fun FlaggedAppsCardLoadingPreview() =
    FlaggedAppsCard(flaggedRows = null, onConfigureClick = {}, modifier = Modifier.padding(16.dp))
