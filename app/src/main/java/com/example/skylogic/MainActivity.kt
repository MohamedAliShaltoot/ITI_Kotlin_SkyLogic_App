package com.example.skylogic

import android.Manifest
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.skylogic.view.mapSelectionView.MapSelectionScreen
import com.example.skylogic.models.Screen
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.ui.theme.SkyLogicTheme
import com.example.skylogic.utils.LocationHelper
import com.example.skylogic.view.alertsView.AlertScreen
import com.example.skylogic.view.alertsView.AlertViewModel
import com.example.skylogic.utils.ConnectivityObserver
import com.example.skylogic.view.favouriteView.FavDetailsView.FavoriteDetailsScreen
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import com.example.skylogic.utils.NetworkState
import com.example.skylogic.view.favouriteView.favView.FavouriteView
import com.example.skylogic.view.settingView.SettingsDataStore
import com.example.skylogic.view.settingView.SettingsScreen
import com.example.skylogic.view.settingView.applyLocale
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModelFactory
import com.example.skylogic.view.splashView.SkyLogicSplashScreen
import com.example.skylogic.view.weatherView.reusable.LocationPermissionDeniedView
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import com.example.skylogic.view.weatherView.reusable.WeatherContent
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dataStore = SettingsDataStore(this)
        val savedLanguage = runBlocking {
            dataStore.language.first()
        }
        val languageCode = if (savedLanguage == "Arabic") "ar" else "en"
        applyLocale(this, languageCode)
        val destination = intent?.getStringExtra("DESTINATION")
        setContent {
            SkyLogicTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        startDestination = destination
                    )
                }
            }
        }
    }
}

@androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun WeatherScreen(
    startDestination: String? = null,
    viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {

    val navController = rememberNavController()
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val alertViewModel: AlertViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var permissionDenied by remember { mutableStateOf(false) }
    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Favourite.route,
        Screen.Alerts.route,
        Screen.Settings.route
    )
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    val connectivityObserver = remember { ConnectivityObserver(context) }
    val networkState by connectivityObserver.observe().collectAsState(
        initial = NetworkState.Available
    )
    val isOffline = networkState is NetworkState.Unavailable
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                locationHelper.getCurrentLocation { location ->
                    location?.let {
                        viewModel.fetchWeather(it.latitude, it.longitude)
                    }
                }
            } else {
                permissionDenied = true
            }
        }

    val savedLat = settingsViewModel.lat.collectAsState().value
    val savedLon = settingsViewModel.lon.collectAsState().value
    val locationMode = settingsViewModel.locationMode.collectAsState().value
    val language     = settingsViewModel.language.collectAsState().value
    val tempUnit     = settingsViewModel.tempUnit.collectAsState().value

    val apiLang = if (language == "Arabic") "ar" else "en"
    val apiUnit = when (tempUnit) {
        "Celsius"    -> "metric"
        "Fahrenheit" -> "imperial"
        else         -> "standard"
    }

    LaunchedEffect(savedLat, savedLon, locationMode, apiLang, apiUnit) {
        if (locationMode == "Map" && savedLat != null && savedLon != null) {
            viewModel.fetchWeather(savedLat, savedLon, apiUnit, apiLang)
        } else if (locationMode == "GPS") {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = when (startDestination) {
                "alerts" -> Screen.Alerts.route
                else -> Screen.Splash.route
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SkyLogicSplashScreen(
                    onSplashComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            } // removes splash from back stack
                        }
                    }
                )
            }

            composable(Screen.Home.route) {

                if (permissionDenied && locationMode == "GPS") {

                    LocationPermissionDeniedView(
                        onRetry = {
                            permissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        },
                        onOpenSettings = {
                            val intent = Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            ).apply {
                                data = Uri.fromParts(
                                    "package",
                                    context.packageName,
                                    null
                                )
                            }
                            context.startActivity(intent)
                        }
                    )

                } else {
                    WeatherContent(
                        viewModel = viewModel,
                        settingsViewModel = settingsViewModel,
                        isOffline = isOffline,
                        navController = navController
                    )
                }
            }
            composable(Screen.MapSelection.route) {
                MapSelectionScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel,
                    weatherViewModel = viewModel,
                    favoriteViewModel = favoriteViewModel
                )
            }

            composable(
                route = "favorite_details/{lat}/{lon}/{name}",
                arguments = listOf(
                    navArgument("lat") { type = NavType.StringType },
                    navArgument("lon") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val lat = backStackEntry.arguments?.getString("lat")?.toDouble() ?: 0.0
                val lon = backStackEntry.arguments?.getString("lon")?.toDouble() ?: 0.0
                val name = backStackEntry.arguments?.getString("name") ?: ""

                FavoriteDetailsScreen(
                    lat = lat,
                    lon = lon,
                    name = name,
                    onBack = { navController.popBackStack() },
                )
            }


            composable(Screen.Favourite.route) {
                FavouriteView(
                    navController = navController,
                    viewModel = favoriteViewModel
                )
            }


            composable(Screen.Alerts.route)  {
                AlertScreen(
                    viewModel = alertViewModel
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    weatherViewModel = viewModel,
                    viewModel = settingsViewModel,
                    navController = navController,
                    isOffline = isOffline
                )
            }
        }
    }
}