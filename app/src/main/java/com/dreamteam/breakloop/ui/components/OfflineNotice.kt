package com.dreamteam.breakloop.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamteam.breakloop.data.network.ConnectivityStatus
import kotlinx.coroutines.delay

private const val RESTORED_MESSAGE = "Internet connection restored"

/**
 * Same look and behavior as the connectivity notice of the Focus tab,
 * but each screen passes its own offline message.
 */
@Composable
fun OfflineNotice(
    status: ConnectivityStatus,
    offlineMessage: String,
) {
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(status, offlineMessage) {
        when (status) {
            ConnectivityStatus.CHECKING -> message = null
            ConnectivityStatus.CONNECTED -> {
                if (message != null && message != RESTORED_MESSAGE) {
                    message = RESTORED_MESSAGE
                    delay(3000)
                    message = null
                }
            }
            ConnectivityStatus.NO_INTERNET,
            ConnectivityStatus.DISCONNECTED -> message = offlineMessage
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .background(color = Color(0xFFFCE6DF), shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = message.orEmpty(),
                color = Color(0xFFB94B2F),
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
            )
        }
    }
}