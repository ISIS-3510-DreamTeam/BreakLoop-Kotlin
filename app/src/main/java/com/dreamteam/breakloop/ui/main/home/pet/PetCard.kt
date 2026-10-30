package com.dreamteam.breakloop.ui.main.home.pet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dreamteam.breakloop.domain.PetMood
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel

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

        if (state.image != null) {
            Image(
                bitmap = state.image,
                contentDescription = "Your pet is ${state.mood.name.lowercase()}",
                contentScale = ContentScale.Fit,
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