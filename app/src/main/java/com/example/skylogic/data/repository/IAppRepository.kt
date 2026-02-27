package com.example.skylogic.data.repository


import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse
import kotlinx.coroutines.flow.Flow

interface IAppRepository {

    // Alert
    suspend fun insertAlert(alert: AlertEntity): Long
    suspend fun deleteAlert(alert: AlertEntity)
    suspend fun getAlertById(id: Int): AlertEntity?
    fun getAllAlerts(): Flow<List<AlertEntity>>

    // Favorite
    suspend fun insertFavorite(favorite: FavoriteEntity)
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    // Cached Weather
    suspend fun insertCachedWeather(weather: CachedWeatherEntity)
    suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity?

    // Cached Forecast
    suspend fun insertCachedForecast(forecast: CachedForecastEntity)
    suspend fun getCachedForecast(locationKey: String): CachedForecastEntity?

    // Remote
    suspend fun getCurrentWeather(lat: Double, lon: Double,units: String = "metric",
                                  lang: String = "en"): CurrentWeatherResponse
    suspend fun getForecast(lat: Double, lon: Double,units: String = "metric",
                            lang: String = "en"): ForecastResponse
    suspend fun getCityCoordinates(city: String): List<GeoResponse>
    suspend fun reverseGeocode(lat: Double, lon: Double): List<GeoResponse>
}