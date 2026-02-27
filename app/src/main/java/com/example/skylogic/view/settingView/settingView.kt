package com.example.skylogic.view.settingView

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skylogic.R
import com.example.skylogic.models.Screen
import com.example.skylogic.utils.GlassCard
import com.example.skylogic.view.settingView.reusable.RadioGroup
import com.example.skylogic.view.settingView.reusable.RadioOption
import com.example.skylogic.view.settingView.reusable.ScreenTitle
import com.example.skylogic.view.settingView.reusable.SettingSectionTitle
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.view.weatherView.reusable.getWeatherGradient
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel,
    viewModel: SettingsViewModel = viewModel(),
    navController: NavController,
    isOffline: Boolean = false
) {

    var showOfflineError by remember { mutableStateOf(false) }
    val locationMode = viewModel.locationMode.collectAsState().value
    val tempUnit = viewModel.tempUnit.collectAsState().value
    val windUnit = viewModel.windUnit.collectAsState().value
    val language = viewModel.language.collectAsState().value
    val condition = weatherViewModel.currentWeather
        ?.weather?.firstOrNull()?.description

    val context = LocalContext.current
    val activity = context as? Activity
    val hasNotificationPermission = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PermissionChecker.PERMISSION_GRANTED
            } else true
        )
    }
    val targetGradient = getWeatherGradient(condition)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission.value = isGranted
        if (!isGranted) {
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
    }

    LaunchedEffect(showOfflineError) {
        if (showOfflineError) {
            Toast.makeText(
                context,
                "No internet connection. Settings cannot be changed offline.",
                Toast.LENGTH_SHORT
            ).show()
            showOfflineError = false
        }
    }
    val animatedColors = targetGradient.map { targetColor ->
        animateColorAsState(targetColor, label = "").value
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(animatedColors))
            .padding(16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            ScreenTitle(stringResource(id = R.string.Settings))
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = stringResource(id = R.string.Location),
                    icon = Icons.Default.LocationOn
                )
                RadioGroup(
                    options = listOf(
                        RadioOption("GPS", stringResource(R.string.GPS)),
                        RadioOption("Map", stringResource(R.string.Map))
                    ),
                    selected = locationMode,
                    onSelect = { selected ->
                        viewModel.setLocationMode(selected)

                        if (selected == "Map") {
                            navController.navigate(Screen.MapSelection.route)
                        }

                    }
                )


            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = stringResource(id = R.string.TemperatureUnit),
                    icon = Icons.Default.Cloud
                )


                RadioGroup(
                    options = listOf(
                        RadioOption("Kelvin",     stringResource(R.string.Kelvin)),
                        RadioOption("Celsius",    stringResource(R.string.Celsius)),
                        RadioOption("Fahrenheit", stringResource(R.string.Fahrenheit))
                    ),
                    selected = tempUnit,
                    onSelect = { selected ->
                        if (isOffline) {
                            showOfflineError = true
                            return@RadioGroup
                        }
                        viewModel.setTempUnit(selected)

                        val apiUnit = when (selected) {
                            "Celsius" -> "metric"
                            "Fahrenheit" -> "imperial"
                            "Kelvin" -> "standard"
                            else -> "metric"
                        }

                        val currentWeather = weatherViewModel.currentWeather

                        currentWeather?.let {
                            weatherViewModel.fetchWeather(
                                lat = it.coord.lat,
                                lon = it.coord.lon,
                                units = apiUnit,
                                lang = if (language == "Arabic") "ar" else "en"
                            )
                        }
                    }
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = stringResource(id = R.string.WindSpeedUnit),
                    icon = Icons.Default.Air
                )


                RadioGroup(
                    options = listOf(
                        RadioOption("meter/sec",  stringResource(R.string.metersec)),
                        RadioOption("miles/hour", stringResource(R.string.mileshour))
                    ),
                    selected = windUnit,
                    onSelect = { viewModel.setWindUnit(it) }
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = stringResource(id = R.string.Language),
                    icon = Icons.Default.Language
                )

                RadioGroup(
                    options = listOf(
                        RadioOption("English", stringResource(R.string.English)),
                        RadioOption("Arabic",  stringResource(R.string.Arabic))
                    ),
                    selected = language,
                    onSelect = { selected ->
                        if (isOffline) {
                            showOfflineError = true
                            return@RadioGroup
                        }
                        viewModel.setLanguage(selected)

                        val languageCode = if (selected == "Arabic") "ar" else "en"
                        applyLocale(context, languageCode)

                        activity?.recreate()
                    }
                )
            }
        }

        // Notification Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    SettingSectionTitle(
                        title = stringResource(R.string.Notification),
                        icon  = Icons.Default.Notifications
                    )
                    RadioGroup(
                        options = listOf(
                            RadioOption("granted", stringResource(R.string.notification_allow)),
                            RadioOption("denied",  stringResource(R.string.notification_deny))
                        ),
                        selected = if (hasNotificationPermission.value) "granted" else "denied",
                        onSelect = { key ->
                            if (key == "granted" && !hasNotificationPermission.value) {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            } else if (key == "denied" && hasNotificationPermission.value) {
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
                        }
                    )
                }
            }}
    }
}