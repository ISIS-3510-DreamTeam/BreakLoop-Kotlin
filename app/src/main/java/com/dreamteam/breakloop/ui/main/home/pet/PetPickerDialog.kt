package com.dreamteam.breakloop.ui.main.home.pet

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.dreamteam.breakloop.remote.PetDto

@Composable
fun PetPickerDialog(
    pets: List<PetDto>,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose your pet") },
        text = {
            Column {
                if (pets.isEmpty()) {
                    Text("Connect to the internet once to see the available pets.")
                }
                for (pet in pets) {
                    TextButton(onClick = { onPick(pet.id) }) { Text(pet.name) }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}