package com.example.skylogic.view.mapSelectionView.mapSelectionViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skylogic.view.mapSelectionView.MapSelectionUiState
import com.example.skylogic.view.mapSelectionView.ReverseGeocodeState
import com.example.skylogic.view.mapSelectionView.SearchState
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MapSelectionViewModel(
    private val weatherViewModel: WeatherViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapSelectionUiState())
    val uiState: StateFlow<MapSelectionUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.length < 2) {
            _uiState.update { it.copy(
                suggestions = emptyList(),
                searchState = SearchState.Idle
            )}
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(searchState = SearchState.Loading) }
            delay(400)
            try {
                val result = weatherViewModel.getCityCoordinates(query)
                _uiState.update { it.copy(
                    suggestions = result,
                    searchState = SearchState.Success(result)
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    suggestions = emptyList(),
                    searchState = SearchState.Error(e.message ?: "Search failed")
                )}
            }
        }
    }

    fun onMapTapped(geoPoint: GeoPoint) {
        _uiState.update { it.copy(
            selectedPoint = geoPoint,
            reverseGeocodeState = ReverseGeocodeState.Loading
        )}
        viewModelScope.launch {
            try {
                val result = weatherViewModel.reverseGeocode(geoPoint.latitude, geoPoint.longitude)
                val cityName = if (result.isNotEmpty()) result[0].name else "Selected Location"
                _uiState.update { it.copy(
                    selectedCityName = cityName,
                    reverseGeocodeState = ReverseGeocodeState.Success(cityName)
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    selectedCityName = "Selected Location",
                    reverseGeocodeState = ReverseGeocodeState.Error(e.message ?: "Geocode failed")
                )}
            }
        }
    }

    fun onSuggestionSelected(name: String, lat: Double, lon: Double) {
        _uiState.update { it.copy(
            selectedPoint = GeoPoint(lat, lon),
            selectedCityName = name,
            suggestions = emptyList(),
            searchState = SearchState.Idle
        )}
    }

    fun onFavoriteAdded() {
        val cityName = _uiState.value.selectedCityName ?: return
        _uiState.update { it.copy(snackbarMessage = "$cityName added to favorites") }
    }

    fun onSnackbarConsumed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

class MapSelectionViewModelFactory(
    private val weatherViewModel: WeatherViewModel
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MapSelectionViewModel(weatherViewModel) as T
}