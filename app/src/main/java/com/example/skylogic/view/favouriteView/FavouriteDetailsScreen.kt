package com.example.skylogic.view.favouriteView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailsScreen(
    lat: Double,
    lon: Double,
    name: String,
    viewModel: FavoriteViewModel = viewModel()
) {

    val key = "$lat,$lon"
//    val forecastMap by viewModel.forecastMap.collectAsState()
//    val forecast = forecastMap[key]

    val forecastMap by viewModel.forecastMap.collectAsState()
    val forecast = forecastMap[key]

//    LaunchedEffect(lat, lon) {
//        viewModel.loadWeatherForFavorite(lat, lon)
//    }
    LaunchedEffect(lat, lon) {
        viewModel.loadWeatherForFavorite(lat, lon)
        viewModel.loadForecast(lat, lon)
    }


    val weatherMap by viewModel.weatherMap.collectAsState()
    val weather = weatherMap[key]


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(name) })
        }
    ) { padding ->

        if (weather == null) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                item {
                    val iconUrl =
                        "https://openweathermap.org/img/wn/${weather.icon}@4x.png"

                    AsyncImage(
                        model = iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "${weather.temp}°C",
                        style = MaterialTheme.typography.displayMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = weather.description,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(forecast ?: emptyList()) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text(item.dt_txt)
                                Text("${item.main.temp}°C")
                            }

                            val iconUrl =
                                "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png"

                            AsyncImage(
                                model = iconUrl,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }
            }

        }
    }
}
/*

val iconUrl =
                    "https://openweathermap.org/img/wn/${weather.icon}@4x.png"

                AsyncImage(
                    model = iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "${weather.temp}°C",
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = weather.description,
                    style = MaterialTheme.typography.titleMedium
                )
 */