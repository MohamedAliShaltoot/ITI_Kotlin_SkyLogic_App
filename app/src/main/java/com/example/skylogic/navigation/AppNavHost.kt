package com.example.skylogic.navigation

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skylogic.view.mapSelectionView.MapSelectionScreen
import com.example.skylogic.models.Screen
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.view.alertsView.AlertScreen
import com.example.skylogic.view.alertsView.AlertViewModel
import com.example.skylogic.view.favouriteView.FavDetailsView.FavoriteDetailsScreen
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import com.example.skylogic.view.favouriteView.favView.FavouriteView
import com.example.skylogic.view.settingView.SettingsScreen
import com.example.skylogic.view.splashView.SkyLogicSplashScreen
import com.example.skylogic.view.weatherView.reusable.LocationPermissionDeniedView
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import com.example.skylogic.view.weatherView.reusable.WeatherContent

@androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String?,
    weatherViewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel,
    favoriteViewModel: FavoriteViewModel,
    alertViewModel: AlertViewModel,
    isOffline: Boolean,
    permissionDenied: Boolean,
    locationMode: String,
    onRetryPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavHost(
        navController  = navController,
        startDestination = when (startDestination) {
            "alerts" -> Screen.Alerts.route
            else     -> Screen.Splash.route
        },
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SkyLogicSplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            if (permissionDenied && locationMode == "GPS") {
                LocationPermissionDeniedView(
                    onRetry = onRetryPermission,
                    onOpenSettings = {
                        val intent = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        ).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            } else {
                WeatherContent(
                    viewModel         = weatherViewModel,
                    settingsViewModel = settingsViewModel,
                    isOffline         = isOffline,
                    navController     = navController
                )
            }
        }

        composable(Screen.MapSelection.route) {
            MapSelectionScreen(
                navController     = navController,
                settingsViewModel = settingsViewModel,
                weatherViewModel  = weatherViewModel,
                favoriteViewModel = favoriteViewModel
            )
        }

        composable(
            route     = "favorite_details/{lat}/{lon}/{name}",
            arguments = listOf(
                navArgument("lat")  { type = NavType.StringType },
                navArgument("lon")  { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat  = backStackEntry.arguments?.getString("lat")?.toDouble()  ?: 0.0
            val lon  = backStackEntry.arguments?.getString("lon")?.toDouble()  ?: 0.0
            val name = backStackEntry.arguments?.getString("name") ?: ""

            FavoriteDetailsScreen(
                lat               = lat,
                lon               = lon,
                name              = name,
                onBack            = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }

        composable(Screen.Favourite.route) {
            FavouriteView(
                navController     = navController,
                viewModel         = favoriteViewModel,
                settingsViewModel = settingsViewModel
            )
        }

        composable(Screen.Alerts.route)  {
            AlertScreen(viewModel = alertViewModel)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                weatherViewModel = weatherViewModel,
                viewModel        = settingsViewModel,
                navController    = navController,
                isOffline        = isOffline
            )
        }
    }
}