package com.example.skylogic

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Radar : Screen("radar")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")
    object MapSelection : Screen("map_selection")
}
