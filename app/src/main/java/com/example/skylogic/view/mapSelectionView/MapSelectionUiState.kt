package com.example.skylogic.view.mapSelectionView


import com.example.skylogic.models.GeoResponse
import org.osmdroid.util.GeoPoint

data class MapSelectionUiState(
    val searchQuery: String = "",
    val suggestions: List<GeoResponse> = emptyList(),
    val searchState: SearchState = SearchState.Idle,
    val selectedPoint: GeoPoint? = null,
    val selectedCityName: String? = null,
    val reverseGeocodeState: ReverseGeocodeState = ReverseGeocodeState.Idle,
    val snackbarMessage: String? = null
)

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val results: List<GeoResponse>) : SearchState()
    data class Error(val message: String) : SearchState()
}

sealed class ReverseGeocodeState {
    object Idle : ReverseGeocodeState()
    object Loading : ReverseGeocodeState()
    data class Success(val cityName: String) : ReverseGeocodeState()
    data class Error(val message: String) : ReverseGeocodeState()
}