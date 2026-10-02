package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.ui.components.BreakLoopChip

@Composable
fun CategoryChips(
    selectedCategory: ActivityCategory?,
    onCategorySelected: (ActivityCategory?) -> Unit
){
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = spacedBy(8.dp)
    ) {
        BreakLoopChip(
            text = "All",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            modifier = Modifier
        )
        for (category in ActivityCategory.entries){
            BreakLoopChip(
                text = category.name,
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                modifier = Modifier
            )
        }
    }
}