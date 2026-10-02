package com.dreamteam.breakloop.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

@Composable
fun TopBar(
    title: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorPalette.Neutral.Snow)
            .statusBarsPadding()                       // don't draw under the clock/battery
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.breakloop_logo),
                contentDescription = "BreakLoop logo",
                modifier = Modifier.size(72.dp),
            )
            Spacer(Modifier.width(10.dp))

            // weight(1f) = take all the space left, which pushes the icon to the right edge
            Column(Modifier.weight(1f)) {
                Text("BREAKLOOP", style = Typography.headlineSmall, color = ColorPalette.Neutral.t850)
                Text(title.uppercase(), style = Typography.headlineMedium, color = ColorPalette.Neutral.t850)
            }

            IconButton(onClick = onSignOut) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = "Sign out",
                    tint = ColorPalette.Neutral.t1000,
                )
            }
        }

        Spacer(Modifier.height(3.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HeaderBadge(text = "🛡️ Shield Test")
            HeaderBadge(text = "🔥 18d")
        }

        Spacer(Modifier.height(6.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(ColorPalette.Neutral.t1000)
        )
    }
}

@Composable
private fun HeaderBadge(
    text: String
) {
    Box(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = ColorPalette.Neutral.t1000,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 5.dp
            )
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
            color = ColorPalette.Neutral.t1000,
            textAlign = TextAlign.Center
        )
    }
}