package com.dreamteam.breakloop.ui.main.profile

import androidx.compose.foundation.layout.Row
import com.dreamteam.breakloop.ui.main.profile.components.ProfileHeader
import com.dreamteam.breakloop.ui.main.achievements.AchievementsUiState
import com.dreamteam.breakloop.ui.main.achievements.AchievementsViewModel
import com.dreamteam.breakloop.ui.main.friends.FriendsViewModel
import com.dreamteam.breakloop.ui.main.profile.components.BadgesCard
import com.dreamteam.breakloop.ui.main.home.pet.PetUiState
import com.dreamteam.breakloop.ui.main.home.pet.PetViewModel
import com.dreamteam.breakloop.ui.main.profile.components.FriendCodeCard
import com.dreamteam.breakloop.ui.main.profile.components.StreakCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.MyProfile
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.profile.components.AvatarWithPet
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun MyProfileScreen(
    streakDays: Int,
    onEditClick: () -> Unit,
    onBack: () -> Unit,
    onBadgesClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
    petViewModel: PetViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel(),
    achievementsViewModel: AchievementsViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val petState by petViewModel.uiState.collectAsStateWithLifecycle()
    val friendsState by friendsViewModel.state.collectAsStateWithLifecycle()
    val achievementsState by achievementsViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
    ) {
        ProfileHeader(title = "My profile", onBack = onBack)

        when (val current = state) {
            ProfileUiState.Loading -> LoadingContent()
            ProfileUiState.NotLoggedIn -> NotLoggedInContent()
            is ProfileUiState.Content -> MyProfileContent(
                profile = current.profile,
                streakDays = streakDays,
                friendCode = friendsState.myFriendCode,
                petState = petState,
                achievementsState = achievementsState,
                onBadgesClick = onBadgesClick,
                onEditClick = onEditClick,
            )
        }
    }
}

@Composable
private fun NotLoggedInContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Log in to see your profile",
            style = MaterialTheme.typography.bodyMedium,
            color = ColorPalette.Neutral.t700,
        )
    }
}

@Composable
private fun MyProfileContent(
    profile: MyProfile?,
    streakDays: Int,
    friendCode: String?,
    petState: PetUiState,
    achievementsState: AchievementsUiState,
    onBadgesClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    val name = profile?.name.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AvatarWithPet(
            name = name,
            localPhotoPath = profile?.localPhotoPath,
            petImage = petState.image,
        )

        // identity: the name leads, everything under it is secondary
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = name.ifBlank { "Your name" },
                style = MaterialTheme.typography.headlineMedium,
                color = ColorPalette.Neutral.t1000,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (petState.image != null && petState.currentPetId != null) {
                Text(
                    text = "with ${petState.nameOf(petState.currentPetId)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorPalette.SpicyPaprika.t700,
                )
            }
            if (profile == null) {
                Hint("Add a photo and a name so your friends recognize you.")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StreakCard(streakDays, modifier = Modifier.weight(1f))
            FriendCodeCard(friendCode, modifier = Modifier.weight(1f))
        }

        BadgesCard(state = achievementsState, onClick = onBadgesClick)

        BreakLoopButton(text = "Edit profile", onClick = onEditClick)

        if (profile != null && profile.pendingUpload) {
            Text(
                text = "Saved on this phone. We'll sync it when you're back online.",
                style = MaterialTheme.typography.bodySmall,
                color = ColorPalette.Neutral.t700,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun Hint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = ColorPalette.Neutral.t700,
        textAlign = TextAlign.Center,
    )
}
