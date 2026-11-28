package com.dreamteam.breakloop.ui.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel

@Composable
fun TopBar(
    title: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    streakDays: Int = 0,
    onProfileClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorPalette.Neutral.Snow)
            .statusBarsPadding()
            .padding(start = 16.dp, end = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.breakloop_logo),
                contentDescription = "BreakLoop logo",
                modifier = Modifier.size(36.dp),
            )
            Spacer(Modifier.width(10.dp))

            Text(
                text = title.uppercase(),
                style = MonoLabel,
                color = ColorPalette.Neutral.t1000,
                modifier = Modifier.weight(1f),
            )

            HeaderBadge(text = null, icon = R.drawable.ic_shield)
            Spacer(Modifier.width(6.dp))
            HeaderBadge(text = "${streakDays}d", icon = R.drawable.ic_streak)

            ProfileMenu(onProfileClick = onProfileClick, onSignOut = onSignOut)
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(ColorPalette.Neutral.t1000)
        )
    }
}

@Composable
private fun ProfileMenu(
    onProfileClick: () -> Unit,
    onSignOut: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Account menu",
                tint = ColorPalette.Neutral.t1000,
                modifier = Modifier.size(24.dp),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = ColorPalette.Neutral.Snow,
            border = BorderStroke(2.dp, ColorPalette.Neutral.t1000),
        ) {
            DropdownMenuItem(
                text = { Text("My profile", color = ColorPalette.Neutral.t1000) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = ColorPalette.Neutral.t1000,
                        modifier = Modifier.size(20.dp),
                    )
                },
                onClick = {
                    expanded = false
                    onProfileClick()
                },
            )
            DropdownMenuItem(
                text = { Text("Sign out", color = ColorPalette.SpicyPaprika.t700) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null,
                        tint = ColorPalette.SpicyPaprika.t700,
                        modifier = Modifier.size(20.dp),
                    )
                },
                onClick = {
                    expanded = false
                    onSignOut()
                },
            )
        }
    }
}

@Composable
private fun HeaderBadge(
    text: String?,
    @DrawableRes icon: Int,
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = ColorPalette.Neutral.t1000,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = ColorPalette.Neutral.t1000,
            modifier = Modifier.size(14.dp),
        )
        if (text != null) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                style = MonoLabel,
                color = ColorPalette.Neutral.t1000,
            )
        }
    }
}
