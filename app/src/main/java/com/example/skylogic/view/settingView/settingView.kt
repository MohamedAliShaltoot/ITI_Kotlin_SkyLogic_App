package com.example.skylogic.view.settingView

import android.os.Build
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skylogic.models.Screen
import com.example.skylogic.utils.GlassCard
import com.example.skylogic.view.settingView.reusable.RadioGroup
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
    navController: NavController
) {

    val locationMode = viewModel.locationMode.collectAsState().value
    val tempUnit = viewModel.tempUnit.collectAsState().value
    val windUnit = viewModel.windUnit.collectAsState().value
    val language = viewModel.language.collectAsState().value
    val condition = weatherViewModel.currentWeather
        ?.weather?.firstOrNull()?.description

    val targetGradient = getWeatherGradient(condition)

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
            ScreenTitle("Settings")
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = "Location",
                    icon = Icons.Default.LocationOn
                )
                RadioGroup(
                    options = listOf("GPS", "Map"),
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
                    title = "Temperature Unit",
                    icon = Icons.Default.Cloud
                )


                RadioGroup(
                    options = listOf("Kelvin", "Celsius", "Fahrenheit"),
                    selected = tempUnit,
                    onSelect = { selected ->

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
                    title = "Wind Speed Unit",
                    icon = Icons.Default.Air
                )


                RadioGroup(
                    options = listOf("meter/sec", "miles/hour"),
                    selected = windUnit,
                    onSelect = { viewModel.setWindUnit(it) }
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = "Language",
                    icon = Icons.Default.Language
                )


                RadioGroup(
                    options = listOf("English", "Arabic"),
                    selected = language,
                    onSelect = { selected ->

                        viewModel.setLanguage(selected)

                        val apiLang =
                            if (selected == "Arabic") "ar" else "en"

                        val apiUnit = when (tempUnit) {
                            "Celsius" -> "metric"
                            "Fahrenheit" -> "imperial"
                            "Kelvin" -> "standard"
                            else -> "metric"
                        }

                        val currentWeather =
                            weatherViewModel.currentWeather

                        currentWeather?.let {
                            weatherViewModel.fetchWeather(
                                lat = it.coord.lat,
                                lon = it.coord.lon,
                                units = apiUnit,
                                lang = apiLang
                            )
                        }
                    }
                )
            }
        }
    }
}