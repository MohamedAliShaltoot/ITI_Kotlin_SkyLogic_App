package com.example.skylogic.view.mapSelectionView

import android.content.Context
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skylogic.models.Screen
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import com.example.skylogic.view.mapSelectionView.mapSelectionViewModel.MapSelectionViewModel
import com.example.skylogic.view.mapSelectionView.mapSelectionViewModel.MapSelectionViewModelFactory
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import com.example.skylogic.R

@Composable
fun MapSelectionScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    weatherViewModel: WeatherViewModel,
    favoriteViewModel: FavoriteViewModel
) {
    val context = LocalContext.current
    val mapViewModel: MapSelectionViewModel = viewModel(
        factory = MapSelectionViewModelFactory(weatherViewModel)
    )

    val uiState by mapViewModel.uiState.collectAsState()

    var currentMarker by remember { mutableStateOf<Marker?>(null) }
    var mapView       by remember { mutableStateOf<MapView?>(null) }

    val currentMarkerRef = rememberUpdatedState(currentMarker)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            mapViewModel.onSnackbarConsumed()
        }
    }

    LaunchedEffect(uiState.reverseGeocodeState) {
        val state = uiState.reverseGeocodeState
        if (state is ReverseGeocodeState.Success) {
            val point = uiState.selectedPoint ?: return@LaunchedEffect
            mapView?.let { mv ->
                currentMarker = placeEnhancedMarker(
                    context       = context,
                    mapView       = mv,
                    point         = point,
                    title         = state.cityName,
                    currentMarker = currentMarkerRef.value   // ← always latest
                )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    shape          = RoundedCornerShape(10.dp),
                    containerColor = Color(0xFF1E88E5),
                    contentColor   = Color.White,
                    modifier       = Modifier.height(56.dp).padding(horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector     = Icons.Default.Favorite,
                            contentDescription = null,
                            tint            = Color.Red,
                            modifier        = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    ) { padding ->

        Box(Modifier.fillMaxSize().padding(padding)) {

            // Map
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    Configuration.getInstance()
                        .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))

                    val mv = MapView(context)
                    mapView = mv
                    mv.setTileSource(TileSourceFactory.MAPNIK)
                    mv.setMultiTouchControls(true)
                    mv.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                    mv.controller.setZoom(5.0)
                    mv.controller.setCenter(GeoPoint(30.0444, 31.2357))

                    val receiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            p?.let { mapViewModel.onMapTapped(it) }
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint?) = false
                    }
                    mv.overlays.add(MapEventsOverlay(receiver))
                    mv
                }
            )

            LaunchedEffect(uiState.selectedPoint) {
                if (uiState.searchState is SearchState.Idle && uiState.selectedPoint != null) {
                    mapView?.controller?.animateTo(uiState.selectedPoint)
                    mapView?.controller?.setZoom(12.0)
                    mapView?.let { mv ->
                        currentMarker = placeEnhancedMarker(
                            context       = context,
                            mapView       = mv,
                            point         = uiState.selectedPoint!!,
                            title         = uiState.selectedCityName ?: "",
                            currentMarker = currentMarkerRef.value
                        )
                    }
                }
            }

            // Back Button
            FloatingActionButton(
                onClick        = { navController.popBackStack() },
                modifier       = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 6.dp),
                containerColor = Color.White,
                shape          = CircleShape,
                elevation      = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4DA3FF))
            }

            // Search Bar + Suggestions
            Column(
                Modifier.align(Alignment.TopCenter).padding(start = 82.dp, end = 16.dp)
            ) {
                FloatingSearchBar(
                    value         = uiState.searchQuery,
                    onValueChange = { mapViewModel.onSearchQueryChanged(it) }
                )

                // Loading indicator under search bar
                if (uiState.searchState is SearchState.Loading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }

                if (uiState.suggestions.isNotEmpty()) {
                    Card(
                        shape     = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                            items(uiState.suggestions) { city ->
                                Text(
                                    text     = "${city.name}, ${city.country}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            mapViewModel.onSuggestionSelected(
                                                name = "${city.name}, ${city.country}",
                                                lat  = city.lat,
                                                lon  = city.lon
                                            )
                                        }
                                        .padding(14.dp)
                                )
                                Divider()
                            }
                        }
                    }
                }

                // Error toast under search
                if (uiState.searchState is SearchState.Error) {
                    Text(
                        text  = (uiState.searchState as SearchState.Error).message,
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Add to Favourites FAB
            FloatingActionButton(
                containerColor = Color.White,
                shape          = CircleShape,
                onClick        = {
                    uiState.selectedPoint?.let {
                        favoriteViewModel.addFavorite(
                            name = uiState.selectedCityName ?: "Selected Location",
                            lat  = it.latitude,
                            lon  = it.longitude
                        )
                        mapViewModel.onFavoriteAdded()
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 90.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Add to favorites", tint = Color.Red)
            }

            // Confirm Location Button
            Button(
                onClick = {
                    uiState.selectedPoint?.let {
                        settingsViewModel.setCustomLocation(it.latitude, it.longitude)
                        settingsViewModel.setLocationMode("Map")
                        navController.navigate(Screen.Home.route)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.Confirm_Location), color = Color.White)
            }
        }
    }
}