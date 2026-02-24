package com.example.skylogic.data.local

import android.content.Context
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSource(context: Context) {
    val alertDao = AppDatabase.getDatabase(context).alertDao()
    val favoriteDao = AppDatabase.getDatabase(context).favoriteDao()
    val cachedWeatherDao = AppDatabase.getDatabase(context).cachedWeatherDao()
    val cachedForecastDao = AppDatabase.getDatabase(context).cachedForecastDao()

    // Alert Methods
    suspend fun insertAlert(alert: AlertEntity) : Long {
        return alertDao.insert(alert)
    }
    suspend fun deleteAlert(alert: AlertEntity) {
        alertDao.delete(alert)
    }
    suspend fun getAlertById(id: Int): AlertEntity? {
        return alertDao.getAlertById(id)
    }
     fun getAllAlerts(): Flow<List<AlertEntity>> {
        return alertDao.getAllAlerts()
    }

    // Favorite Methods
    suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }
    suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favoriteDao.deleteFavorite(favorite)
    }
    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    // Cached Weather Methods
    suspend fun insertCachedWeather(weather: CachedWeatherEntity) {
        cachedWeatherDao.insert(weather)
    }
    suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity? {
        return cachedWeatherDao.getWeather(locationKey)

    }

    // Cached Forecast Methods
    suspend fun insertCachedForecast(forecast: CachedForecastEntity) {
        cachedForecastDao.insert(forecast)
    }
    suspend fun getCachedForecast(locationKey: String): CachedForecastEntity? {
        return cachedForecastDao.getForecast(locationKey)
    }
}


