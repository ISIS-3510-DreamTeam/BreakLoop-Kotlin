package com.dreamteam.breakloop.ui.layout

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
            NavigationBarItem(
                selected = tab == currentTab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                label = { Text(stringResource(tab.label)) }
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
