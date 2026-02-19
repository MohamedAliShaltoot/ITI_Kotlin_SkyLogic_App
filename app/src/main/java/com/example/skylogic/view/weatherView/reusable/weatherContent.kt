package com.example.skylogic.view.weatherView.reusable

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherContent(
    viewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel
)

{

    val weather = viewModel.currentWeather
    val hourly = viewModel.getHourlyData()
    val daily = viewModel.getDailyData()
    val condition = weather?.weather?.firstOrNull()?.description
    val targetGradient = getWeatherGradient(condition)
    val windUnit = settingsViewModel.windUnit.collectAsState().value
val isLoading = viewModel.isLoading
    val animatedColors = targetGradient.map { targetColor ->
        animateColorAsState(targetColor, label = "").value
    }

    Scaffold(
        containerColor = Color.Transparent,

        ) { innerPadding ->
        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color =  Color(0xFF4DA3FF)
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(animatedColors)
                    )

                    .padding(innerPadding)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    Spacer(Modifier.height(16.dp))
                }

                weather?.let {

                    item {
                        HeaderSection(it)
                        Spacer(Modifier.height(24.dp))
                    }

                    item {
                        CurrentWeatherSection(it, windUnit)

                        Spacer(Modifier.height(28.dp))
                    }
                }
                item {

                    Text(
                        "HOURLY FORECAST",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(hourly) { item ->
                            ExpandableHourlyCard(item)
                        }
                    }

                    Spacer(Modifier.height(28.dp))
                }
                item {
                    SevenDayForecastSection(daily)
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}