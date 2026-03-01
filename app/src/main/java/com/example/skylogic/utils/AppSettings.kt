package com.example.skylogic.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel

data class AppSettings(
    val savedLat: Double?,
    val savedLon: Double?,
    val locationMode: String,
    val apiUnit: String,
    val apiLang: String
)

@Composable
fun rememberAppSettings(settingsViewModel: SettingsViewModel): AppSettings {
    val savedLat     = settingsViewModel.lat.collectAsState().value
    val savedLon     = settingsViewModel.lon.collectAsState().value
    val locationMode = settingsViewModel.locationMode.collectAsState().value
    val language     = settingsViewModel.language.collectAsState().value
    val tempUnit     = settingsViewModel.tempUnit.collectAsState().value

    val apiLang = if (language == "Arabic") "ar" else "en"
    val apiUnit = when (tempUnit) {
        "Celsius"    -> "metric"
        "Fahrenheit" -> "imperial"
        else         -> "standard"
    }

    return AppSettings(savedLat, savedLon, locationMode, apiUnit, apiLang)
}