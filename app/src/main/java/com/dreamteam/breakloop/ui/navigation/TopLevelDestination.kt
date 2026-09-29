package com.dreamteam.breakloop.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dreamteam.breakloop.R

enum class TopLevelDestination(
    val route: Any,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    HOME(Home, R.string.home, R.drawable.ic_nav_hearth),
    FOCUS(Focus, R.string.focus, R.drawable.ic_nav_focus),
    STATS(Stats, R.string.stats, R.drawable.ic_nav_stats),
    OFFLINE(Offline, R.string.offline, R.drawable.ic_nav_offline),
    FRIENDS(Friends, R.string.friends, R.drawable.ic_nav_friends)
}
