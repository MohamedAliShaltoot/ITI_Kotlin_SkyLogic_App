package com.example.skylogic.view.favouriteView

import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.models.ForecastItem


sealed class FavListUiState {
    object Loading : FavListUiState()
    object Empty : FavListUiState()
    data class Success(val favorites: List<FavoriteEntity>) : FavListUiState()
    data class Error(val message: String) : FavListUiState()
}

sealed class FavDetailsUiState {
    object Loading : FavDetailsUiState()
    data class Success(
        val weather: CachedWeatherEntity,
        val forecast: List<ForecastItem>
    ) : FavDetailsUiState()
    object NoCache : FavDetailsUiState()
    data class Error(val message: String) : FavDetailsUiState()
}