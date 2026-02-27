package com.example.skylogic.data.repository


import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAppRepository : IAppRepository {

    var weatherException: Exception? = null
    var forecastException: Exception? = null

    var fakeWeather: CurrentWeatherResponse? = null
    var fakeForecastResponse: ForecastResponse? = null

    var fakeCachedWeather: CachedWeatherEntity? = null
    var fakeCachedForecast: CachedForecastEntity? = null

    private val _alerts = MutableStateFlow<List<AlertEntity>>(emptyList())


    override suspend fun insertAlert(alert: AlertEntity): Long {
        val newList = _alerts.value + alert.copy(id = (_alerts.value.size + 1))
        _alerts.value = newList
        return newList.last().id.toLong()
    }

    override suspend fun deleteAlert(alert: AlertEntity) {
        _alerts.value = _alerts.value.filter { it.id != alert.id }
    }

    override suspend fun getAlertById(id: Int): AlertEntity? =
        _alerts.value.find { it.id == id }

    override fun getAllAlerts(): Flow<List<AlertEntity>> = _alerts

    private val favorites = mutableListOf<FavoriteEntity>()

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        favorites.add(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favorites.remove(favorite)
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> =
        MutableStateFlow(favorites.toList())


    override suspend fun insertCachedWeather(weather: CachedWeatherEntity) {
        fakeCachedWeather = weather
    }

    override suspend fun getCachedWeather(locationKey: String): CachedWeatherEntity? =
        fakeCachedWeather

    override suspend fun insertCachedForecast(forecast: CachedForecastEntity) {
        fakeCachedForecast = forecast
    }

    override suspend fun getCachedForecast(locationKey: String): CachedForecastEntity? =
        fakeCachedForecast

    override suspend fun getCurrentWeather(
        lat: Double, lon: Double, units: String, lang: String
    ): CurrentWeatherResponse {
        weatherException?.let { throw it }
        return fakeWeather ?: error("fakeWeather not set in test")
    }

    override suspend fun getForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): ForecastResponse {
        forecastException?.let { throw it }
        return fakeForecastResponse ?: error("fakeForecastResponse not set in test")
    }

    override suspend fun getCityCoordinates(city: String): List<GeoResponse> = emptyList()

    override suspend fun reverseGeocode(lat: Double, lon: Double): List<GeoResponse> = emptyList()
}