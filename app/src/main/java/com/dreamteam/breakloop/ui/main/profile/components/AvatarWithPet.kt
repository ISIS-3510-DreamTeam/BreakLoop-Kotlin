package com.dreamteam.breakloop.ui.main.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.ui.theme.ColorPalette

// the profile avatar with the pet sitting on its corner; no badge until there is a pet image
@Composable
fun AvatarWithPet(
    name: String,
    localPhotoPath: String?,
    petImage: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        ProfileAvatar(name = name, localPhotoPath = localPhotoPath, size = 168.dp)

        if (petImage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp)
                    .size(56.dp)
                    .background(ColorPalette.Neutral.Snow, CircleShape)
                    .border(2.dp, ColorPalette.Neutral.t1000, CircleShape)
                    .padding(7.dp),
            ) {
                Image(
                    bitmap = petImage,
                    contentDescription = "Your pet",
                    contentScale = ContentScale.Fit,
                    filterQuality = FilterQuality.None, // keeps pixel art sharp
                    modifier = Modifier.size(42.dp),
                )
            }
        }
    }
}
