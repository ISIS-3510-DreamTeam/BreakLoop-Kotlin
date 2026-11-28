package com.dreamteam.breakloop.ui.main.profile.components

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoTitle
import java.io.File

@Composable
fun ProfileAvatar(
    name: String,
    localPhotoPath: String?,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
    remotePhotoUrl: String? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(ColorPalette.GoldenOrange.t500)
            .border(2.dp, ColorPalette.Neutral.t1000, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (localPhotoPath != null) {
            AsyncImage(
                model = File(localPhotoPath),
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, ColorPalette.Neutral.t1000, CircleShape),
            )
        } else {
            // initials stay underneath while the remote photo loads, or if it can't
            Text(
                text = initials(name),
                style = MonoTitle.copy(fontSize = (size.value * 0.28f).sp),
                color = ColorPalette.Neutral.t1000,
            )
            if (!remotePhotoUrl.isNullOrBlank()) {
                RemotePhoto(url = remotePhotoUrl, size = size)
            }
        }
    }
}

@Composable
private fun RemotePhoto(url: String, size: Dp) {
    val context = LocalContext.current
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    // asking for the on-screen size keeps the decoded image in memory small
    val request = remember(context, url, sizePx) {
        ImageRequest.Builder(context)
            .data(url)
            .size(sizePx)
            .crossfade(true)
            .build()
    }
    AsyncImage(
        model = request,
        contentDescription = "Profile photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .border(2.dp, ColorPalette.Neutral.t1000, CircleShape),
    )
}

// first letter of the first two words, "?" when there is no name yet
private fun initials(name: String): String =
    name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifEmpty { "?" }
