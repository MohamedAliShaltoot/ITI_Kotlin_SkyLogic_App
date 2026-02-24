package com.example.skylogic.models

sealed class Screen(val route: String) {
    object Splash       : Screen("splash")
    object Home : Screen("home")
    object Favourite : Screen("favourite")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")
    object MapSelection : Screen("map_selection")
}