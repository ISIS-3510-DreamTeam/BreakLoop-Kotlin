package com.dreamteam.breakloop.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.navigation.TopLevelDestination
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme

@Composable
fun NavBar(
    currentTab: TopLevelDestination?,
    onTabSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier) {
        TopLevelDestination.entries.forEach { tab ->
            val selected = tab == currentTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Image(
                        painter = painterResource(if (selected) tab.selectedIcon else tab.icon),
                        contentDescription = null,
                        modifier = Modifier.height(24.dp),
                    )
                },
                label = { Text(stringResource(tab.label)) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavBarPreview() {
    BreakLoopTheme {
        NavBar(
            currentTab = TopLevelDestination.HOME,
            onTabSelected = {}
        )
    }
}