package com.example.skylogic.models

import com.example.skylogic.R

sealed class Screen(val route: String, val labelRes: Int) {
    object Home      : Screen("home",      R.string.nav_home)
    object Favourite : Screen("favourite", R.string.nav_favourite)
    object Alerts    : Screen("alerts",    R.string.nav_alerts)
    object Settings  : Screen("settings",  R.string.nav_settings)
    object Splash       : Screen("splash",        0)
    object MapSelection : Screen("map_selection", 0)
}