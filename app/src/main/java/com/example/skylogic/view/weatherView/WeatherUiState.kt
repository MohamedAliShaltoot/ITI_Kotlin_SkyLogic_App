package com.example.skylogic.view.weatherView

import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastItem

sealed class WeatherUiState {
    object Loading : WeatherUiState()

    data class Success(
        val weather: CurrentWeatherResponse,
        val forecast: List<ForecastItem>
    ) : WeatherUiState()

    data class CachedSuccess(          // offline, showing cached data
        val weather: CurrentWeatherResponse,
        val forecast: List<ForecastItem>
    ) : WeatherUiState()

    data class Error(
        val message: String
    ) : WeatherUiState()
}