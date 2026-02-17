package com.example.skylogic.view.mapSelectionView.mapSelectionViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skylogic.models.GeoResponse
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapSelectionViewModel(
    private val weatherViewModel: WeatherViewModel
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _suggestions = MutableStateFlow<List<GeoResponse>>(emptyList())
    val suggestions: StateFlow<List<GeoResponse>> = _suggestions

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        searchJob?.cancel()

        if (query.length < 2) {
            _suggestions.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(400) // debounce

            try {
                val result = weatherViewModel.getCityCoordinates(query)
                _suggestions.value = result
            } catch (e: Exception) {
                _suggestions.value = emptyList()
            }
        }
    }

    fun reverseGeocode(
        lat: Double,
        lon: Double,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = weatherViewModel.reverseGeocode(lat, lon)
                if (result.isNotEmpty()) {
                    onResult(result[0].name)
                } else {
                    onResult("Selected Location")
                }
            } catch (e: Exception) {
                onResult("Selected Location")
            }
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}
class MapSelectionViewModelFactory(
    private val weatherViewModel: WeatherViewModel
) : androidx.lifecycle.ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapSelectionViewModel(weatherViewModel) as T
    }
}
