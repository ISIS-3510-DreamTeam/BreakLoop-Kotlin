package com.dreamteam.breakloop.ui.main.friends

import androidx.compose.material3.HorizontalDivider
import com.dreamteam.breakloop.ui.components.StreakIcon
import androidx.compose.ui.text.style.TextOverflow
import com.dreamteam.breakloop.ui.components.PixelArt
import com.dreamteam.breakloop.ui.components.PixelIcon
import com.dreamteam.breakloop.ui.main.profile.components.ProfileAvatar
import com.dreamteam.breakloop.ui.theme.MonoTitle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.Friend
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopChip
import com.dreamteam.breakloop.ui.components.BreakLoopTextField
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.MonoLabel
import com.dreamteam.breakloop.ui.theme.SpaceMono

private const val CODE_LENGTH = 4

@Composable
fun FriendsScreen(
    modifier: Modifier = Modifier,
    viewModel: FriendsViewModel = viewModel(),
    onFriendClick: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FriendsContent(
        state = state,
        onAddFriend = viewModel::addFriend,
        onRefresh = { viewModel.loadFriends(forceRefresh = true) },
        onMessageShown = viewModel::clearMessage,
        modifier = modifier,
        onFriendClick = onFriendClick,
    )
}

@Composable
fun FriendsContent(
    state: FriendsUiState,
    onAddFriend: (String) -> Unit,
    onRefresh: () -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier,
    onFriendClick: (String) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = ColorPalette.Neutral.Snow,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "code") { FriendCodeCard(state.myFriendCode) }
                item(key = "add") { AddFriendRow(onAddFriend) }
                if (state.pendingCodes.isNotEmpty()) {
                    item(key = "pending") { PendingCodes(state.pendingCodes) }
                }
                item(key = "status") { StatusBar(state, onRefresh) }

                if (state.friends.isEmpty()) {
                    item(key = "empty") { EmptyState(state.isLoading) }
                } else {
                    item(key = "friends") { FriendList(state.friends, onFriendClick) }
                }
            }
        }
    }
}

@Composable
private fun FriendCodeCard(code: String?) {
    val clipboard = LocalClipboardManager.current
    BreakLoopCard(
        modifier = Modifier.fillMaxWidth(),
        elevated = false,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    "FRIEND CODE",
                    style = MaterialTheme.typography.labelSmall,
                    color = ColorPalette.Neutral.t700,
                )
                if (code == null) {
                    Text(
                        "Connect to the internet to get your code",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorPalette.Neutral.t700,
                    )
                } else {
                    Text(text = code, style = MonoTitle, color = ColorPalette.SpicyPaprika.t700)
                    Text(
                        "Share it so friends can add you",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorPalette.Neutral.t700,
                    )
                }
            }
            if (code != null) {
                IconButton(onClick = { clipboard.setText(AnnotatedString(code)) }) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = "Copy friend code",
                        tint = ColorPalette.Neutral.t1000,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddFriendRow(onAddFriend: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BreakLoopTextField(
            value = text,
            onValueChange = { text = it.uppercase().take(CODE_LENGTH) },
            label = "Friend code",
            placeholder = "ABCD",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            modifier = Modifier.weight(1f),
        )
        BreakLoopButton(
            text = "Add",
            onClick = {
                onAddFriend(text)
                text = ""
            },
            enabled = text.isNotBlank(),
            modifier = Modifier
                .width(96.dp)
                .padding(bottom = 4.dp),
        )
    }
}

@Composable
private fun PendingCodes(codes: Set<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            codes.forEach {
                BreakLoopChip(
                    text = it,
                    selected = false,
                    onClick = {},
                    modifier = Modifier,
                )
            }
        }
        Text(
            "Will be added when you’re back online",
            style = MaterialTheme.typography.bodySmall,
            color = ColorPalette.Neutral.t700,
        )
    }
}

@Composable
private fun StatusBar(state: FriendsUiState, onRefresh: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        if (state.isOffline) {
            Text(
                text = "Offline · updated ${updatedAgo(state.lastUpdated)}",
                style = MaterialTheme.typography.bodySmall,
                color = ColorPalette.Neutral.t700,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorPalette.GoldenOrange.t100)
                    .padding(vertical = 4.dp),
            )
        } else if (state.isLoading && state.friends.isNotEmpty()) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = ColorPalette.SpicyPaprika.t500,
                trackColor = ColorPalette.SpicyPaprika.t100,
            )
        }
    }
}

private fun updatedAgo(lastUpdated: Long?): String {
    if (lastUpdated == null) return "never"
    val minutes = ((System.currentTimeMillis() - lastUpdated) / 60_000).coerceAtLeast(0)
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        minutes < 60 * 24 -> "${minutes / 60} h ago"
        else -> "${minutes / (60 * 24)} d ago"
    }
}

@Composable
private fun EmptyState(isLoading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = ColorPalette.SpicyPaprika.t500)
        } else {
            PixelIcon(PixelArt.PAW, size = 48.dp)
            Text(
                "No friends yet. Share your code to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorPalette.Neutral.t700,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FriendList(friends: List<Friend>, onFriendClick: (String) -> Unit) {
    // longest streak first; ties by name so the order doesn't jump around
    val ranked = remember(friends) {
        friends.sortedWith(
            compareByDescending<Friend> { it.streak }.thenBy { it.displayName.lowercase() }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "YOUR FRIENDS · ${friends.size} · BY STREAK",
            style = MaterialTheme.typography.labelSmall,
            color = ColorPalette.Neutral.t700,
        )
        BreakLoopCard(modifier = Modifier.fillMaxWidth(), elevated = false, contentPadding = 2.dp) {
            ranked.forEachIndexed { index, friend ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = ColorPalette.Neutral.t100,
                    )
                }
                FriendRow(friend, onClick = { onFriendClick(friend.uid) })
            }
        }
    }
}

@Composable
private fun FriendRow(friend: Friend, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClickLabel = "Open profile", onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // same avatar as the friend's profile
        ProfileAvatar(name = friend.displayName, localPhotoPath = null, size = 48.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                friend.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = ColorPalette.Neutral.t1000,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Pet level ${friend.petLevel}",
                style = MaterialTheme.typography.bodySmall,
                color = ColorPalette.Neutral.t700,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            StreakIcon(size = 18.dp)
            Text(
                "${friend.streak} d",
                style = MonoLabel,
                color = ColorPalette.Neutral.t1000,
            )
        }
    }
}

private val previewFriends = listOf(
    Friend("1", "Ana", 12, 3),
    Friend("2", "Bruno", 4, 1),
)

private val previewState = FriendsUiState(
    myFriendCode = "K7QX",
    friends = previewFriends,
    lastUpdated = System.currentTimeMillis() - 5 * 60_000,
    pendingCodes = emptySet(),
    isOffline = false,
    isLoading = false,
    message = null,
)

@Preview(showBackground = true)
@Composable
private fun FriendsPreviewList() {
    FriendsContent(previewState, {}, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun FriendsPreviewOfflinePending() {
    FriendsContent(
        previewState.copy(isOffline = true, pendingCodes = setOf("AB12", "ZZ99")),
        {}, {}, {},
    )
}

@Preview(showBackground = true)
@Composable
private fun FriendsPreviewEmpty() {
    FriendsContent(previewState.copy(friends = emptyList(), myFriendCode = null), {}, {}, {})
}

@Preview(showBackground = true)
@Composable
private fun FriendsPreviewLoading() {
    FriendsContent(previewState.copy(friends = emptyList(), isLoading = true), {}, {}, {})
}
