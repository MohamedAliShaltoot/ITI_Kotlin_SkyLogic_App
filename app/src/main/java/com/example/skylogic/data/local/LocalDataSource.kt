package com.example.skylogic.data.local

import android.content.Context
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSource(context: Context, database: AppDatabase = AppDatabase.getDatabase(context)) : ILocalDataSource {
    val alertDao = database.alertDao()
    val favoriteDao = database.favoriteDao()
    val cachedWeatherDao = database.cachedWeatherDao()
    val cachedForecastDao = database.cachedForecastDao()

    // Alert Methods
    override suspend fun insertAlert(alert: AlertEntity) : Long {
        return alertDao.insert(alert)
    }
    override suspend fun deleteAlert(alert: AlertEntity) {
        alertDao.delete(alert)
    }
    override suspend fun getAlertById(id: Int): AlertEntity? {
        return alertDao.getAlertById(id)
    }
    override fun getAllAlerts(): Flow<List<AlertEntity>> {
        return alertDao.getAllAlerts()
    }

    // Favorite Methods
    override  suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }
    override  suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favoriteDao.deleteFavorite(favorite)
    }
    override  fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    // Cached Weather Methods
    override   suspend fun insertCachedWeather(weather: CachedWeatherEntity) {
        cachedWeatherDao.insert(weather)
    }
    override  suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity? {
        return cachedWeatherDao.getWeather(locationKey)

    }

    // Cached Forecast Methods
    override   suspend fun insertCachedForecast(forecast: CachedForecastEntity) {
        cachedForecastDao.insert(forecast)
    }
    override  suspend fun getCachedForecast(locationKey: String): CachedForecastEntity? {
        return cachedForecastDao.getForecast(locationKey)
    }
}


