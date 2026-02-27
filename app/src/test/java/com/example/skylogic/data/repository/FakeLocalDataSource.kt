package com.example.skylogic.data.repository

import com.example.skylogic.data.local.ILocalDataSource
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocalDataSource : ILocalDataSource {

    // Alerts
    private val _alerts = MutableStateFlow<List<AlertEntity>>(emptyList())

    override suspend fun insertAlert(alert: AlertEntity): Long {
        val newId = (_alerts.value.maxOfOrNull { it.id } ?: 0) + 1
        _alerts.value = _alerts.value + alert.copy(id = newId)
        return newId.toLong()
    }

    override suspend fun deleteAlert(alert: AlertEntity) {
        _alerts.value = _alerts.value.filter { it.id != alert.id }
    }

    override suspend fun getAlertById(id: Int): AlertEntity? =
        _alerts.value.find { it.id == id }

    override fun getAllAlerts(): Flow<List<AlertEntity>> = _alerts

    // Favorites
    private val _favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        _favorites.value = _favorites.value + favorite
    }

    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        _favorites.value = _favorites.value.filter { it != favorite }
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> = _favorites

    // Cached Weather
    private val cachedWeathers = mutableMapOf<String, CachedWeatherEntity>()

    override suspend fun insertCachedWeather(weather: CachedWeatherEntity) {
        cachedWeathers[weather.locationKey] = weather
    }

    override suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity? =
        cachedWeathers[locationKey]

    // Cached Forecast
    private val cachedForecasts = mutableMapOf<String, CachedForecastEntity>()

    override suspend fun insertCachedForecast(forecast: CachedForecastEntity) {
        cachedForecasts[forecast.locationKey] = forecast
    }

    override suspend fun getCachedForecast(locationKey: String): CachedForecastEntity? =
        cachedForecasts[locationKey]
}