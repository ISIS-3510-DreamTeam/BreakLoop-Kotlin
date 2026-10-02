package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.ui.components.BreakLoopChip
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun CategoryChips(
    selectedCategory: ActivityCategory?,
    onCategorySelected: (ActivityCategory?) -> Unit
){
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconChip(
            icon = Icons.Outlined.Apps,
            text = "All",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) }
        )
        for (category in ActivityCategory.entries){
            IconChip(
                icon = categoryIcon(category),
                text = category.name,
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun IconChip(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) ColorPalette.SpicyPaprika.t500 else ColorPalette.Neutral.t700,
            modifier = Modifier.size(14.dp)
        )
        BreakLoopChip(
            text = text,
            selected = selected,
            onClick = onClick,
            modifier = Modifier
        )
    }
}