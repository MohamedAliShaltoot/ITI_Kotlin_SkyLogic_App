package com.example.skylogic.view.weatherView.reusable

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skylogic.R
import com.example.skylogic.models.Screen
import com.example.skylogic.utils.OfflineBanner
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.view.weatherView.WeatherUiState
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherContent(
    viewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel,
    isOffline: Boolean = false,
    navController: NavController
) {
    val uiState  by viewModel.uiState.collectAsState()
    val windUnit by settingsViewModel.windUnit.collectAsState()
    val tempUnit by settingsViewModel.tempUnit.collectAsState()
    val weather = when (val s = uiState) {
        is WeatherUiState.Success       -> s.weather
        is WeatherUiState.CachedSuccess -> s.weather
        else                            -> null
    }

    val condition      = weather?.weather?.firstOrNull()?.description
    val targetGradient = getWeatherGradient(condition)
    val animatedColors = targetGradient.map { animateColorAsState(it, label = "").value }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            when (uiState) {

                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFF4DA3FF)
                        )
                    }
                }

                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(animatedColors))
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚠ ${(uiState as WeatherUiState.Error).message}",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }

                is WeatherUiState.Success,
                is WeatherUiState.CachedSuccess -> {
                    val hourly = viewModel.getHourlyData()
                    val daily  = viewModel.getDailyData()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(animatedColors))
                            .padding(innerPadding)
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isOffline) {
                            item {
                                AnimatedVisibility(
                                    visible = isOffline,
                                    enter   = expandVertically() + fadeIn(),
                                    exit    = shrinkVertically() + fadeOut()
                                ) {
                                    OfflineBanner()
                                }
                            }
                        }

                        item { Spacer(Modifier.height(16.dp)) }

                        weather?.let {
                            item {
                                HeaderSection(it)
                                Spacer(Modifier.height(24.dp))
                            }
                            item {


                                CurrentWeatherSection(it, windUnit, tempUnit)
                              //  CurrentWeatherSection(it, windUnit)
                                Spacer(Modifier.height(28.dp))
                            }
                        }

                        item {
                            Text(
                                stringResource(R.string.HOURLYFORECAST),
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(hourly) { item ->
                                    ExpandableHourlyCard(item, windUnit, tempUnit)
                                   // ExpandableHourlyCard(item)
                                }
                            }
                            Spacer(Modifier.height(28.dp))
                        }

                        item {
                            SevenDayForecastSection(daily,tempUnit = tempUnit,
                                windUnit = windUnit)
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }

                else -> {}
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(Screen.MapSelection.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp),
            containerColor = Color(0xFF4DA3FF),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Open Map",
                tint = Color.White
            )
        }

    }
}