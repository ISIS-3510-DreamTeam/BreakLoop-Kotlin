package com.dreamteam.breakloop.ui.main.stats.flaggedapps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppIcon(
    packageName: String,
    size: Dp,
    modifier: Modifier = Modifier,
    label: String = packageName
) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current
    val icon by produceState<ImageBitmap?>(initialValue = null, packageName) {
        if (inPreview) return@produceState
        value = withContext(Dispatchers.IO) {
            try {
                val px = with(context.resources.displayMetrics) { (size.value * density).toInt() }
                context.packageManager.getApplicationIcon(packageName)
                    .toBitmap(px, px)
                    .asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    val shape = RoundedCornerShape(size * 0.25f)
    val bitmap = icon
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(ColorPalette.GoldenOrange.t100),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label.firstOrNull()?.uppercase() ?: "?",
                style = Typography.titleMedium.copy(fontSize = (size.value * 0.4f).sp),
                color = ColorPalette.SpicyPaprika.t700
            )
        }
    }
}
