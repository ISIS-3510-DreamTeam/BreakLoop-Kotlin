package com.dreamteam.breakloop.ui.main.home.pet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.PetMood
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.MonoLabel
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun PetCard(
    state: PetUiState,
    onChangePet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BreakLoopCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "YOUR PET", style = MonoLabel, modifier = Modifier.weight(1f))
            TextButton(onClick = onChangePet) {
                Text(if (state.hasPet) "Change" else "Choose")
            }
        }

        if (!state.hasPet) {
            Text("You don't have a pet yet. Choose one to start caring for it!")
            return@BreakLoopCard
        }

        // The image is saved in a local val so Kotlin knows it is not null inside the if
        val image = state.image
        if (image != null) {
            AnimatedPet(
                image = image,
                mood = state.mood,
                modifier = Modifier.fillMaxWidth().height(180.dp),
            )
        } else {
            Text("Your pet is on its way. It will appear once you're online.")
        }

        Spacer(Modifier.height(8.dp))

        val name = state.nameOf(state.currentPetId)
        if (state.mood == PetMood.HAPPY) {
            Text("$name is happy!", color = ColorPalette.Fern.t500, style = MaterialTheme.typography.bodyLarge)
        } else {
            val missing = state.xpGoal - state.xpToday
            Text("Earn $missing more XP today to cheer $name up.", style = MaterialTheme.typography.bodyLarge)
        }
        Text("XP today: ${state.xpToday} / ${state.xpGoal}", color = ColorPalette.Neutral.t700)

        if (state.pendingPetId != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${state.nameOf(state.pendingPetId)} will arrive as soon as you're online.",
                color = ColorPalette.GoldenOrange.t500,
            )
        }
    }
}

// Idle animation: happy = quick hop and stretch, sad = slow sink and sway.
@Composable
private fun AnimatedPet(
    image: ImageBitmap,
    mood: PetMood,
    modifier: Modifier = Modifier,
) {
    val happy = mood == PetMood.HAPPY
    val transition = rememberInfiniteTransition(label = "petIdle")

    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (happy) -10f else 4f,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween<Float>(durationMillis = if (happy) 450 else 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "offsetY",
    )

    val stretch by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (happy) 1.05f else 0.97f,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween<Float>(durationMillis = if (happy) 450 else 1600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "stretch",
    )

    val tilt by transition.animateFloat(
        initialValue = if (happy) 0f else -3f,
        targetValue = if (happy) 0f else 3f,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween<Float>(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "tilt",
    )

    Image(
        bitmap = image,
        contentDescription = "Your pet is ${mood.name.lowercase()}",
        contentScale = ContentScale.Fit,
        filterQuality = FilterQuality.None, // keeps pixel art sharp
        modifier = modifier.graphicsLayer {
            translationY = offsetY.dp.toPx()
            scaleY = stretch
            rotationZ = tilt
            transformOrigin = TransformOrigin(0.5f, 1f) // squash from the feet
        },
    )
}