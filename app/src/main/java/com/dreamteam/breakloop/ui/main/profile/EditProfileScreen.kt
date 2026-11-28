package com.dreamteam.breakloop.ui.main.profile

import com.dreamteam.breakloop.ui.main.profile.components.ProfileHeader
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopTextField
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.profile.components.ProfileAvatar
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel

private const val MAX_NAME_LENGTH = 20

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = state
    val isSaving = current is ProfileUiState.Content && current.isSaving

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) viewModel.onPhotoPicked(uri)
    }

    // leaving while the photo is being saved would cancel the save
    BackHandler(enabled = isSaving) {}

    val snackbarHostState = remember { SnackbarHostState() }
    val message = if (current is ProfileUiState.Content) current.message else null
    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            // TODO: call viewModel.clearMessage() once ProfileViewModel has it
        }
    }

    Scaffold(
        containerColor = ColorPalette.Neutral.Snow,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ProfileHeader(title = "Edit profile", onBack = onBack, backEnabled = !isSaving)

            when (current) {
                ProfileUiState.Loading -> LoadingContent()
                ProfileUiState.NotLoggedIn -> Text(
                    "Log in to see your profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorPalette.Neutral.t700,
                    modifier = Modifier.padding(16.dp),
                )
                is ProfileUiState.Content -> EditProfileForm(
                    content = current,
                    onChangePhoto = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onSave = { name ->
                        viewModel.onSaveName(name)
                        // a blank name is rejected: stay so the error can be seen
                        if (name.isNotBlank()) onBack()
                    },
                )
            }
        }
    }
}

@Composable
private fun EditProfileForm(
    content: ProfileUiState.Content,
    onChangePhoto: () -> Unit,
    onSave: (String) -> Unit,
) {
    // orEmpty(): the first photo turns a null profile into one with an empty name,
    // and that must not reset what the user already typed
    val savedName = content.profile?.name.orEmpty()
    var name by remember(savedName) { mutableStateOf(savedName.take(MAX_NAME_LENGTH)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.clickable(enabled = !content.isSaving, onClick = onChangePhoto),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                ProfileAvatar(name = savedName, localPhotoPath = content.profile?.localPhotoPath)
                if (content.isSaving) {
                    CircularProgressIndicator(color = ColorPalette.SpicyPaprika.t500)
                }
            }
            Text("Change photo", style = MonoLabel, color = ColorPalette.SpicyPaprika.t700)
        }

        BreakLoopTextField(
            value = name,
            onValueChange = { name = it.take(MAX_NAME_LENGTH) },
            label = "Name",
            placeholder = "Your name",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        )

        Text(
            text = "${name.length}/$MAX_NAME_LENGTH",
            style = MaterialTheme.typography.bodySmall,
            color = ColorPalette.Neutral.t700,
            modifier = Modifier.align(Alignment.End),
        )

        BreakLoopButton(
            text = "Save",
            onClick = { onSave(name) },
            enabled = !content.isSaving,
        )

        Text(
            "Changes are saved on this phone first and synced when you're online.",
            style = MaterialTheme.typography.bodySmall,
            color = ColorPalette.Neutral.t700,
            textAlign = TextAlign.Center,
        )
    }
}
