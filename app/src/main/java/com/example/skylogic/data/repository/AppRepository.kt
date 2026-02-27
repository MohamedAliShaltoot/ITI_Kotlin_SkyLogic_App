package com.example.skylogic.data.repository

import com.example.skylogic.data.local.ILocalDataSource
import com.example.skylogic.data.local.LocalDataSource
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.data.remote.IRemoteDataSource
import com.example.skylogic.data.remote.RemoteDataSource
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse
import kotlinx.coroutines.flow.Flow

class AppRepository(val localDataSource: ILocalDataSource, val remoteDataSource: IRemoteDataSource) : IAppRepository {

    // localDataSource :

    // Alert Methods
  override  suspend fun insertAlert(alert: AlertEntity) : Long {
       return localDataSource.insertAlert(alert)
    }
    override  suspend fun deleteAlert(alert: AlertEntity) {
        localDataSource.deleteAlert(alert)
    }
    override suspend fun getAlertById(id: Int): AlertEntity? {
        return localDataSource.getAlertById(id)
    }
    override fun getAllAlerts(): Flow<List<AlertEntity>> {
        return localDataSource.getAllAlerts()
    }

    // Favorite Methods
    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        localDataSource.insertFavorite(favorite)
    }
    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        localDataSource.deleteFavorite(favorite)
    }
    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return localDataSource.getAllFavorites()
    }

    // Cached Weather Methods
    override suspend fun insertCachedWeather(weather: CachedWeatherEntity) {
        localDataSource.insertCachedWeather(weather)
    }
    override  suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity? {
        return localDataSource.getCachedWeather(locationKey)

    }

    // Cached Forecast Methods
    override suspend fun insertCachedForecast(forecast: CachedForecastEntity) {
        localDataSource.insertCachedForecast(forecast)
    }
    override  suspend fun getCachedForecast(locationKey: String): CachedForecastEntity? {
        return localDataSource.getCachedForecast(locationKey)
    }

    // remoteDataSource
    override  suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String ,
        lang: String
    ): CurrentWeatherResponse {
        return remoteDataSource.getCurrentWeather(lat, lon, units, lang)
    }

    override   suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String ,
        lang: String
    ): ForecastResponse {
        return remoteDataSource.getForecast(lat, lon, units, lang)
    }

    override   suspend fun getCityCoordinates(city: String): List<GeoResponse> {
        return remoteDataSource.getCityCoordinates(city)
    }

    override  suspend fun reverseGeocode(
        lat: Double,
        lon: Double
    ): List<GeoResponse> {
        return remoteDataSource.reverseGeocode(lat, lon)
    }



}