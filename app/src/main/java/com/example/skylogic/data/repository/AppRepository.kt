package com.example.skylogic.data.repository

import com.example.skylogic.data.local.LocalDataSource
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.data.remote.RemoteDataSource
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse
import kotlinx.coroutines.flow.Flow

class AppRepository(val localDataSource: LocalDataSource , val remoteDataSource: RemoteDataSource) {

    // localDataSource :

    // Alert Methods
    suspend fun insertAlert(alert: AlertEntity) : Long {
       return localDataSource.insertAlert(alert)
    }
    suspend fun deleteAlert(alert: AlertEntity) {
        localDataSource.deleteAlert(alert)
    }
    suspend fun getAlertById(id: Int): AlertEntity? {
        return localDataSource.getAlertById(id)
    }
    fun getAllAlerts(): Flow<List<AlertEntity>> {
        return localDataSource.getAllAlerts()
    }

    // Favorite Methods
    suspend fun insertFavorite(favorite: FavoriteEntity) {
        localDataSource.insertFavorite(favorite)
    }
    suspend fun deleteFavorite(favorite: FavoriteEntity) {
        localDataSource.deleteFavorite(favorite)
    }
    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return localDataSource.getAllFavorites()
    }

    // Cached Weather Methods
    suspend fun insertCachedWeather(weather: CachedWeatherEntity) {
        localDataSource.insertCachedWeather(weather)
    }
    suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity? {
        return localDataSource.getCachedWeather(locationKey)

    }

    // Cached Forecast Methods
    suspend fun insertCachedForecast(forecast: CachedForecastEntity) {
        localDataSource.insertCachedForecast(forecast)
    }
    suspend fun getCachedForecast(locationKey: String): CachedForecastEntity? {
        return localDataSource.getCachedForecast(locationKey)
    }

    // remoteDataSource
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String = "metric",
        lang: String = "en"
    ): CurrentWeatherResponse {
        return remoteDataSource.getCurrentWeather(lat, lon, units, lang)
    }

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String = "metric",
        lang: String = "en"
    ): ForecastResponse {
        return remoteDataSource.getForecast(lat, lon, units, lang)
    }

    suspend fun getCityCoordinates(city: String): List<GeoResponse> {
        return remoteDataSource.getCityCoordinates(city)
    }

    suspend fun reverseGeocode(
        lat: Double,
        lon: Double
    ): List<GeoResponse> {
        return remoteDataSource.reverseGeocode(lat, lon)
    }



}