package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.enums.ActivityCategory

@Composable
fun CategoryChips(
    selectedCategory: ActivityCategory?,
    onCategorySelected: (ActivityCategory?) -> Unit
){
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            text = "All",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) }
        )
        for (category in ActivityCategory.entries){
            FilterChip(
                text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
                )
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)
    Text(
        text = text,
        style = Typography.labelMedium,
        color = if (selected) ColorPalette.Neutral.t1000 else ColorPalette.Neutral.t700,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) ColorPalette.Neutral.t100 else Color.Transparent, shape)
            .border(1.5.dp, if (selected) ColorPalette.Neutral.t1000 else ColorPalette.Neutral.t200, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}
