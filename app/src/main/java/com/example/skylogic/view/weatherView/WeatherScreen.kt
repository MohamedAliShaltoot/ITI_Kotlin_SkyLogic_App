package com.example.skylogic.view.weatherView

import android.Manifest
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.skylogic.models.Screen
import com.example.skylogic.navigation.AppNavHost
import com.example.skylogic.navigation.BottomNavBar
import com.example.skylogic.utils.ConnectivityObserver
import com.example.skylogic.utils.LocationHelper
import com.example.skylogic.utils.NetworkState
import com.example.skylogic.utils.WeatherFetchEffect
import com.example.skylogic.utils.rememberAppSettings
import com.example.skylogic.utils.rememberLocationPermissionHandler
import com.example.skylogic.view.alertsView.AlertViewModel
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModelFactory
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModelFactory
import kotlin.collections.contains

@androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun WeatherScreen(
    startDestination: String? = null,
    weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val navController      = rememberNavController()
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val alertViewModel: AlertViewModel       = viewModel()
    val context            = LocalContext.current
    val locationHelper     = remember { LocationHelper(context) }

    // Network
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val networkState by connectivityObserver.observe().collectAsState(NetworkState.Available)
    val isOffline = networkState is NetworkState.Unavailable

    // Settings
    val settings = rememberAppSettings(settingsViewModel)

    // Permission state
    var permissionDenied by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLocationPermissionHandler(
        locationHelper    = locationHelper,
        onLocationReady   = { lat, lon ->
            if (!lat.isNaN()) {
                weatherViewModel.fetchWeather(lat, lon, settings.apiUnit, settings.apiLang)
            } else {
                weatherViewModel.setError("Could not get location. Please try again or use Map mode.")
            }
        },
        onPermissionDenied = { permissionDenied = true },
        apiUnit = settings.apiUnit,
        apiLang = settings.apiLang
    )

    // Fetch trigger
    WeatherFetchEffect(
        settings          = settings,
        weatherViewModel  = weatherViewModel,
        permissionLauncher = permissionLauncher
    )

    // Nav back stack for bottom bar visibility
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarScreens = listOf(
        Screen.Home.route, Screen.Favourite.route,
        Screen.Alerts.route, Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) BottomNavBar(navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController     = navController,
            startDestination  = startDestination,
            weatherViewModel  = weatherViewModel,
            settingsViewModel = settingsViewModel,
            favoriteViewModel = favoriteViewModel,
            alertViewModel    = alertViewModel,
            isOffline         = isOffline,
            permissionDenied  = permissionDenied,
            locationMode      = settings.locationMode,
            onRetryPermission = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
