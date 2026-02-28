package com.example.skylogic.utils
import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel


@Composable
fun WeatherFetchEffect(
    settings: AppSettings,
    weatherViewModel: WeatherViewModel,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    LaunchedEffect(
        settings.savedLat,
        settings.savedLon,
        settings.locationMode,
        settings.apiLang,
        settings.apiUnit
    ) {
        when {
            settings.locationMode == "Map"
                    && settings.savedLat != null
                    && settings.savedLon != null -> {
                weatherViewModel.fetchWeather(
                    settings.savedLat,
                    settings.savedLon,
                    settings.apiUnit,
                    settings.apiLang
                )
            }
            settings.locationMode == "GPS" -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}