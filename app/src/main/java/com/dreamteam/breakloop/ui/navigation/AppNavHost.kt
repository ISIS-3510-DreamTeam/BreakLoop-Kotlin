package com.dreamteam.breakloop.ui.navigation

import com.dreamteam.breakloop.ui.main.friends.profile.FriendProfileScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dreamteam.breakloop.ui.account.auth.AuthScreen
import com.dreamteam.breakloop.ui.main.focus.FocusScreen
import com.dreamteam.breakloop.ui.main.friends.FriendsScreen
import com.dreamteam.breakloop.ui.main.home.HomeScreen
import com.dreamteam.breakloop.ui.main.offline.OfflineScreen
import com.dreamteam.breakloop.ui.main.offline.components.ActivityDetailScreen
import com.dreamteam.breakloop.ui.main.profile.EditProfileScreen
import com.dreamteam.breakloop.ui.main.profile.MyProfileScreen
import com.dreamteam.breakloop.ui.main.stats.StatsScreen
import com.dreamteam.breakloop.ui.main.stats.flaggedapps.FlaggedAppsRoute
import com.dreamteam.breakloop.ui.main.stats.deepstats.DeepStatsScreen
import com.dreamteam.breakloop.ui.main.achievements.AchievementsScreen
import com.dreamteam.breakloop.ui.main.achievements.detail.AchievementDetailScreen

// the default cross-fade lasts 700 ms, which feels like lag
private const val FADE_MS = 150

private fun NavBackStackEntry.isFullScreen(): Boolean =
    destination.hasRoute<MyProfile>() || destination.hasRoute<EditProfile>() ||
        destination.hasRoute<Achievements>() || destination.hasRoute<AchievementDetail>()

// going in or out of a screen without bars resizes the content, so switch without animating
private fun AnimatedContentTransitionScope<NavBackStackEntry>.togglesBars(): Boolean =
    initialState.isFullScreen() != targetState.isFullScreen()

@Composable
fun AppNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    // read through a State so a streak change doesn't rebuild the navigation graph
    val currentStreakDays by rememberUpdatedState(streakDays)

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) MainGraph else AuthGraph,
        modifier = modifier,
        enterTransition = {
            if (togglesBars()) EnterTransition.None else fadeIn(tween(FADE_MS))
        },
        exitTransition = {
            if (togglesBars()) ExitTransition.None else fadeOut(tween(FADE_MS))
        }
    ) {
        navigation<AuthGraph>(startDestination = Login) {

            composable<Splash> {
                // TODO: splash screen
            }

            composable<Login> {
                AuthScreen()
            }
        }

        navigation<MainGraph>(startDestination = Home) {

            composable<Home> {
                HomeScreen(
                    onAchievementsClick = {
                        navController.navigate(Achievements)
                    }
                )
            }

            composable<Achievements> {
                AchievementsScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onAchievementClick = { id ->
                        navController.navigate(AchievementDetail(id))
                    }
                )
            }

            composable<AchievementDetail> {
                AchievementDetailScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<Focus> {
                FocusScreen()
            }

            composable<Stats> {
                StatsScreen(
                    onDeepStatsClick = {
                        navController.navigate(DeepStats)
                    },
                    onConfigureFlaggedAppsClick = {
                        navController.navigate(FlaggedApps)
                    }
                )
            }

            composable<FlaggedApps> {
                FlaggedAppsRoute(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<DeepStats> {
                DeepStatsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable<Offline> {
                OfflineScreen(
                    onActivityClick = { id, fromSuggestion, interactionId ->
                        navController.navigate(
                            OfflineActivityDetail(
                                activityId = id,
                                fromSuggestion = fromSuggestion,
                                interactionId = interactionId
                            )
                        )
                    }
                )
            }

            composable<Friends> {
                FriendsScreen(
                    onFriendClick = { uid ->
                        navController.navigate(FriendProfileRoute(uid)) { launchSingleTop = true }
                    }
                )
            }

            composable<FriendProfileRoute> {
                FriendProfileScreen(
                    onBack = {
                        navController.popBackStack<FriendProfileRoute>(inclusive = true)
                    }
                )
            }

            composable<MyProfile> {
                MyProfileScreen(
                    streakDays = currentStreakDays,
                    onEditClick = {
                        navController.navigate(EditProfile) { launchSingleTop = true }
                    },
                    onBack = {
                        navController.popBackStack<MyProfile>(inclusive = true)
                    },
                    onBadgesClick = {
                        navController.navigate(Achievements) { launchSingleTop = true }
                    }
                )
            }

            composable<EditProfile> {
                EditProfileScreen(
                    onBack = {
                        // popping by route makes a double tap harmless
                        navController.popBackStack<EditProfile>(inclusive = true)
                    }
                )
            }

            composable<OfflineActivityDetail> {
                ActivityDetailScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onGoToActivity = {
                        id -> navController.navigate(OfflineActivityDetail(id, fromSuggestion = false)){
                            // reemplaza el detalle actual: con launchSingleTop reutilizaría la misma entrada
                            // (y su ViewModel) y la pantalla no cambiaría
                            popUpTo<OfflineActivityDetail> { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}