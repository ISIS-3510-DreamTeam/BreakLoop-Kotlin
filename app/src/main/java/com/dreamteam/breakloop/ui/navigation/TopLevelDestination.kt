package com.dreamteam.breakloop.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dreamteam.breakloop.R

enum class TopLevelDestination(
    val route: Any,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,

) {
    HOME(Home, R.string.home, R.drawable.ic_nav_hearth, R.drawable.ic_nav_hearth_selected ),
    FOCUS(Focus, R.string.focus, R.drawable.ic_nav_focus, R.drawable.ic_nav_focus_selected),
    STATS(Stats, R.string.stats, R.drawable.ic_nav_stats, R.drawable.ic_nav_stats_selected),
    OFFLINE(Offline, R.string.offline, R.drawable.ic_nav_offline, R.drawable.ic_nav_offline_selected),
    FRIENDS(Friends, R.string.friends, R.drawable.ic_nav_friends, R.drawable.ic_nav_friends_selected)
}
