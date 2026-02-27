package com.example.skylogic.data.local


import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun insertAlert(alert: AlertEntity): Long
    suspend fun deleteAlert(alert: AlertEntity)
    suspend fun getAlertById(id: Int): AlertEntity?
    fun getAllAlerts(): Flow<List<AlertEntity>>

    suspend fun insertFavorite(favorite: FavoriteEntity)
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    suspend fun insertCachedWeather(weather: CachedWeatherEntity)
    suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity?

    suspend fun insertCachedForecast(forecast: CachedForecastEntity)
    suspend fun getCachedForecast(locationKey: String): CachedForecastEntity?
}