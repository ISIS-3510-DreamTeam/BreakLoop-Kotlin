package com.dreamteam.breakloop.ui.main.achievements.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.domain.AchievementProgress

/** Medal image from the backend, loaded and cached by Coil. Locked medals are gray and dimmed. */
@Composable
fun AchievementMedal(
    progress: AchievementProgress,
    modifier: Modifier = Modifier,
    onImageError: () -> Unit = {},
) {
    val context = LocalContext.current
    val placeholder = rememberVectorPainter(Icons.Outlined.EmojiEvents)
    val grayscale = remember { ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) }

    AsyncImage(
        model = RetrofitInstance.BASE_URL + progress.achievement.image,
        contentDescription = progress.achievement.name,
        imageLoader = AchievementImageLoader.get(context),
        modifier = modifier,
        placeholder = placeholder,
        error = placeholder,
        onError = { onImageError() },
        contentScale = ContentScale.Fit,
        filterQuality = FilterQuality.None, // keeps the pixel art sharp
        colorFilter = if (progress.unlocked) null else grayscale,
        alpha = if (progress.unlocked) 1f else 0.45f,
    )
}