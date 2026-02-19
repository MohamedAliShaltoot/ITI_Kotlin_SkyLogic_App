package com.example.skylogic.view.favouriteView

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.skylogic.data.local.FavoriteEntity
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.Screen
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel

@Composable
fun FavouriteView(
    navController: NavController,
    viewModel: FavoriteViewModel = viewModel()
) {

    val favorites by viewModel.favorites.collectAsState()
    val weatherMap by viewModel.weatherMap.collectAsState()
val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color(0xFF4DA3FF),
                shape = CircleShape,
                onClick = {
                    navController.navigate(Screen.MapSelection.route)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add place to Favourite",
                    modifier = Modifier.size(30.dp),
                    //tint = Color.White
                    )
              //  Text("+")
            }
        }
    ) { padding ->

        if (favorites.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorite locations yet")
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                items(
                    items = favorites,
                    key = { "${it.lat},${it.lon}" }
                )
                { favorite ->

                    FavoriteItem(
                        favorite = favorite,
                        weather = weatherMap[favorite.name],
                        onDelete = {
                            viewModel.deleteFavorite(favorite)
                            Toast.makeText(
                                context,
                                "${favorite.name} removed from favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onClick = {
                            navController.navigate(
                                "favorite_details/${favorite.lat}/${favorite.lon}/${favorite.name}"
                            )
                        },

                                onAppear = {
                            viewModel.loadWeatherForFavorite(
                                favorite.lat,
                                favorite.lon,
                                favorite.name
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    favorite: FavoriteEntity,
    weather: CurrentWeatherResponse?,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onAppear: () -> Unit
) {

    LaunchedEffect(favorite.lat, favorite.lon) {
        onAppear()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = favorite.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(6.dp))

                weather?.let {
                    Text(
                        text = "${it.main.temp}°C",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = it.weather[0].description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            weather?.let {
                val iconCode = it.weather[0].icon
                val iconUrl =
                    "https://openweathermap.org/img/wn/${iconCode}@2x.png"

                AsyncImage(
                    model = iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}
