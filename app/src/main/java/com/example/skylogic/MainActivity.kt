package com.example.skylogic

import android.Manifest
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import com.example.skylogic.view.favouriteView.FavDetailsView.FavoriteDetailsScreen
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import com.example.skylogic.view.favouriteView.favView.FavouriteView
import com.example.skylogic.view.settingView.SettingsScreen
import com.example.skylogic.view.weatherView.reusable.LocationPermissionDeniedView
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import com.example.skylogic.view.weatherView.reusable.WeatherContent

class MainActivity : ComponentActivity() {
    @androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    viewModel: WeatherViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
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

//    val permissionLauncher =
//        rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission()
//        ) { isGranted ->
//            if (isGranted) {
//                locationHelper.getCurrentLocation { location ->
//                    location?.let {
//                        viewModel.fetchWeather(it.latitude, it.longitude)
//                    }
//                }
//            }
//        }
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

    LaunchedEffect(savedLat, savedLon, locationMode) {

        if (locationMode == "Map" && savedLat != null && savedLon != null) {

            viewModel.fetchWeather(savedLat, savedLon)

        } else if (locationMode == "GPS") {

            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
//        bottomBar = {
//            BottomNavBar(navController)
//        }
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
          //  startDestination = Screen.Splash.route,
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
                            popUpTo(Screen.Splash.route) { inclusive = true } // removes splash from back stack
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
                        settingsViewModel = settingsViewModel
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
                navController = navController


            )
        }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        Screen.Home,
        Screen.Favourite,
        Screen.Alerts,
        Screen.Settings
    )

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        items.forEach { screen ->

            val selected = currentRoute == screen.route

            BottomItem(
                label = screen.route.replaceFirstChar { it.uppercase() },
                icon = when (screen) {
                    Screen.Home -> Icons.Default.Home
                    Screen.Favourite -> Icons.Default.Favorite
                    Screen.Alerts -> Icons.Default.Notifications
                    Screen.Settings -> Icons.Default.Settings
                    else -> Icons.Default.Home
                },
                selected = selected
            ) {
                navController.navigate(screen.route) {
                    popUpTo(Screen.Home.route)
                    launchSingleTop = true
                }
            }
        }
    }
}
@Composable
fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val tint by animateColorAsState(
        if (selected) Color(0xFF4DA3FF) else Color.Gray,
        label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Icon(icon, null, tint = tint)
        Text(label.uppercase(), color = tint, fontSize = 12.sp)
    }
}


