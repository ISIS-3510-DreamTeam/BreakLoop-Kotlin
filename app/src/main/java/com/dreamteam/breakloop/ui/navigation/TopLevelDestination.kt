package com.dreamteam.breakloop.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dreamteam.breakloop.R

enum class TopLevelDestination(
    val route: Any,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    HOME(Home, R.string.home, R.drawable.ic_home),
    FOCUS(Focus, R.string.focus, R.drawable.ic_home),
    PROFILE(Profile, R.string.profile, R.drawable.ic_home),
}