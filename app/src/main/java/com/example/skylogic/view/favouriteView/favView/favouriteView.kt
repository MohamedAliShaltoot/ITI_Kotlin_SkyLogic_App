package com.example.skylogic.view.favouriteView.favView



import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.skylogic.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skylogic.models.Screen
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteView(
    navController: NavController,
    viewModel: FavoriteViewModel = viewModel()
) {
    //val favorites by viewModel.favorites.collectAsState()
    val favorites by viewModel.getAllFavorites().collectAsState(
        initial = emptyList()
    )
    val weatherMap by viewModel.weatherMap.collectAsState()
    val context = LocalContext.current

    // Sky gradient background
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(FavouriteViewColors.SkyMid, FavouriteViewColors.SkyDeep),
        center = Offset(0f, 0f),
        radius = 1800f
    )

    Scaffold(
        // Top App Bar
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(FavouriteViewColors.SkyAccent, FavouriteViewColors.SkyLight)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Icons.Default.Favorite",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.FavoriteLocations),
                                color = FavouriteViewColors.TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                letterSpacing = 0.3.sp
                            )
                            if (favorites.isNotEmpty()) {
                                Text(
                                    text = "${favorites.size} ${stringResource(R.string.savedPlace)}${if (favorites.size > 1) stringResource(R.string.s) else ""}",
                                    color = FavouriteViewColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FavouriteViewColors.SkyDeep,
                    titleContentColor = FavouriteViewColors.TextPrimary
                ),
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    ambientColor = FavouriteViewColors.SkyAccent.copy(alpha = 0.3f)
                )
            )
        },
        //FAB
        floatingActionButton = {
            FloatingActionButton(
                containerColor = FavouriteViewColors.SkyAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp)
                    .shadow(12.dp, CircleShape, ambientColor =FavouriteViewColors. SkyAccent),
                onClick = { navController.navigate(Screen.MapSelection.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add place to Favourite",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            if (favorites.isEmpty()) {
                // Empty State
                EmptyFavoritesState(modifier = Modifier.padding(padding))

            } else {
                // List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 100.dp // space above FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = favorites,
                        key = { _, item -> "${item.lat},${item.lon}" }
                    ) { index, favorite ->
                        val key = "${favorite.lat},${favorite.lon}"

                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(index * 60L)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                        ) {
                            FavoriteItem(
                                favorite = favorite,
                                weather = weatherMap[key],
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
                                    viewModel.loadWeatherForFavorite(favorite.lat, favorite.lon)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

