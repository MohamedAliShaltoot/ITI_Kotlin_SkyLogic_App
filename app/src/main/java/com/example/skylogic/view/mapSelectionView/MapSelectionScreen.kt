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
import androidx.compose.material3.TextField
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skylogic.models.Screen
import com.example.skylogic.view.settingView.settingViewModel.SettingsViewModel
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import com.example.skylogic.view.mapSelectionView.mapSelectionViewModel.MapSelectionViewModel
import com.example.skylogic.view.mapSelectionView.mapSelectionViewModel.MapSelectionViewModelFactory
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*


@Composable
fun MapSelectionScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    weatherViewModel: WeatherViewModel,
    favoriteViewModel: FavoriteViewModel
)
 {

    val context = LocalContext.current

    val mapViewModel: MapSelectionViewModel = viewModel(
        factory = MapSelectionViewModelFactory(weatherViewModel)
    )

    val searchQuery by mapViewModel.searchQuery.collectAsState()
    val suggestions by mapViewModel.suggestions.collectAsState()
     var selectedCityName by remember { mutableStateOf<String?>(null) }

    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var marker by remember { mutableStateOf<Marker?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
     val snackbarHostState = remember { SnackbarHostState() }
     val scope = rememberCoroutineScope()
     Scaffold(
         snackbarHost = {
             SnackbarHost(hostState = snackbarHostState) { data ->
                 Snackbar(
                     shape = RoundedCornerShape(20.dp),
                     containerColor = Color(0xFF1E88E5),
                     contentColor = Color.White
                 ) {
                     Row(verticalAlignment = Alignment.CenterVertically) {

                         Icon(
                             imageVector = Icons.Default.Favorite,
                             contentDescription = null,
                             tint = Color.Red,
                             modifier = Modifier.size(40.dp)
                         )

                         Spacer(Modifier.width(8.dp))

                         Text(
                             text = data.visuals.message,
                             style = MaterialTheme.typography.bodyMedium
                         )
                     }
                 }
             }
         }
     ) { padding ->

         Box(
             Modifier
                 .fillMaxSize()
                 .padding(padding)
         ) {


         AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {

                Configuration.getInstance()
                    .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))

                val mv = MapView(context)
                mapView = mv

                mv.setTileSource(TileSourceFactory.MAPNIK)
                mv.setMultiTouchControls(true)
                mv.zoomController.setVisibility(
                    CustomZoomButtonsController.Visibility.ALWAYS
                )

                mv.controller.setZoom(5.0)
                mv.controller.setCenter(GeoPoint(30.0444, 31.2357))

                val receiver = object : MapEventsReceiver {

                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {

                        p?.let { geoPoint ->
                            selectedPoint = geoPoint

                            mapViewModel.reverseGeocode(
                                geoPoint.latitude,
                                geoPoint.longitude
                            ) { cityName ->
                                selectedCityName = cityName
                                marker?.let { mv.overlays.remove(it) }

                                val newMarker = Marker(mv)
                                newMarker.position = geoPoint
                                newMarker.setAnchor(
                                    Marker.ANCHOR_CENTER,
                                    Marker.ANCHOR_BOTTOM
                                )
                                newMarker.title = cityName
                                newMarker.showInfoWindow()

                                mv.overlays.add(newMarker)
                                marker = newMarker
                                mv.invalidate()
                            }
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?) = false
                }

                mv.overlays.add(MapEventsOverlay(receiver))
                mv
            }
        )

        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {

            TextField(
                value = searchQuery,
                onValueChange = { mapViewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search city...") },
                shape = RoundedCornerShape(20.dp)
            )

            if (suggestions.isNotEmpty()) {

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {

                        items(suggestions) { city ->

                            Text(
                                text = "${city.name}, ${city.country}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {

                                        val geoPoint =
                                            GeoPoint(city.lat, city.lon)

                                        selectedPoint = geoPoint
                                        mapViewModel.clearSuggestions()

                                        mapView?.controller?.animateTo(geoPoint)
                                        mapView?.controller?.setZoom(12.0)

                                        marker?.let {
                                            mapView?.overlays?.remove(it)
                                        }

                                        val newMarker = Marker(mapView)
                                        newMarker.position = geoPoint
                                        newMarker.setAnchor(
                                            Marker.ANCHOR_CENTER,
                                            Marker.ANCHOR_BOTTOM
                                        )
                                        selectedCityName = "${city.name}, ${city.country}"

                                        newMarker.title =
                                            "${city.name}, ${city.country}"
                                        newMarker.showInfoWindow()

                                        mapView?.overlays?.add(newMarker)
                                        marker = newMarker
                                        mapView?.invalidate()
                                    }
                                    .padding(14.dp)
                            )
                            Divider()
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            containerColor = Color.White,
            shape = CircleShape,
            onClick = {
                selectedPoint?.let {

                   // val cityName = searchQuery.ifBlank { "Custom Location" }
                    val cityName = selectedCityName ?: "Selected Location"

                    favoriteViewModel.addFavorite(
                        name = cityName,
                        lat = it.latitude,
                        lon = it.longitude
                    )
                    // show SnackBar
                  //  Toast.makeText(context, "${cityName} added to favorites", Toast.LENGTH_SHORT).show()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "$cityName added to favorites",
                            duration = SnackbarDuration.Short
                        )
                    }

                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp),

        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Add to favorites",
                tint = Color.Red
            )
        }

        Button(
            onClick = {
                selectedPoint?.let {
                    settingsViewModel.setCustomLocation(
                        it.latitude,
                        it.longitude
                    )
                    settingsViewModel.setLocationMode("Map")
                    navController.navigate(Screen.Home.route)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C853)
            ),
            shape = RoundedCornerShape(16.dp)
        )
        {
            Text("Confirm Location", color = Color.White)
        }
    }
         }
}
