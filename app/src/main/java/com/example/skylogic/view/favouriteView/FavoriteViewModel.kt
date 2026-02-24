package com.example.skylogic.view.favouriteView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skylogic.data.local.AppDatabase
import com.example.skylogic.data.local.LocalDataSource
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.remote.RemoteDataSource
import com.example.skylogic.data.remote.RetrofitInstance
import com.example.skylogic.data.repository.AppRepository
import com.example.skylogic.models.ForecastItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application)
    : AndroidViewModel(application) {
    private val remote = RemoteDataSource()
    private val local = LocalDataSource(application)
    private val appRepository = AppRepository(local ,remote)

    private val _forecastMap =
        MutableStateFlow<Map<String, List<ForecastItem>>>(emptyMap())

    val forecastMap: StateFlow<Map<String, List<ForecastItem>>> =
        _forecastMap

    private val _weatherMap =
        MutableStateFlow<Map<String, CachedWeatherEntity>>(emptyMap())
    val weatherMap: StateFlow<Map<String, CachedWeatherEntity>> =
        _weatherMap


     fun getAllFavorites() : Flow<List<FavoriteEntity>> {
        return appRepository.getAllFavorites().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }


    fun addFavorite(name: String, lat: Double, lon: Double) {

        viewModelScope.launch {

            appRepository.insertFavorite(
                FavoriteEntity(
                    name = name,
                    lat = lat,
                    lon = lon
                )
            )
        }
    }

    fun deleteFavorite(favorite: FavoriteEntity) {

        viewModelScope.launch {
            appRepository.deleteFavorite(favorite)
        }
    }

    fun loadWeatherForFavorite(lat: Double, lon: Double) {

        viewModelScope.launch {

            val key = "$lat,$lon"

            val cached = appRepository.getCachedWeather(key)

            val now = System.currentTimeMillis()
            val isFresh =
                cached != null && (now - cached.timestamp) < 30 * 60 * 1000

            if (isFresh) {
                _weatherMap.value =
                    _weatherMap.value.toMutableMap().apply {
                        put(key, cached)
                    }
                return@launch
            }

            try {
                val response =
                    RetrofitInstance.api.getCurrentWeather(lat, lon)

                val entity = CachedWeatherEntity(
                    locationKey = key,
                    name = response.name,
                    temp = response.main.temp,
                    feelsLike = response.main.feels_like,
                    humidity = response.main.humidity,
                    pressure = response.main.pressure,
                    windSpeed = response.wind.speed,
                   // clouds = response.,
                    description = response.weather[0].description,
                    icon = response.weather[0].icon,
                    timestamp = now
                )


                appRepository.insertCachedWeather(entity)

                _weatherMap.value =
                    _weatherMap.value.toMutableMap().apply {
                        put(key, entity)
                    }

            } catch (e: Exception) {
                if (cached != null) {
                    _weatherMap.value =
                        _weatherMap.value.toMutableMap().apply {
                            put(key, cached)
                        }
                }
            }
        }
    }
fun loadForecast(lat: Double, lon: Double) {

    val key = "$lat,$lon"

    viewModelScope.launch {

        //val cached = forecastDao.getForecast(key)
        val cached = appRepository.getCachedForecast(key)


        val now = System.currentTimeMillis()
        val isFresh =
            cached != null && (now - cached.timestamp) < 30 * 60 * 1000

        if (isFresh) {

            val list: List<ForecastItem> =
                Gson().fromJson(
                    cached.forecastJson,
                    object : TypeToken<List<ForecastItem>>() {}.type
                )

            _forecastMap.value =
                _forecastMap.value.toMutableMap().apply {
                    put(key, list)
                }

            return@launch
        }

        try {

            val response =
                RetrofitInstance.api.getForecast(lat, lon)

            val json = Gson().toJson(response.list)

            appRepository.insertCachedForecast(
                CachedForecastEntity(
                    locationKey = key,
                    forecastJson = json,
                    timestamp = now
                )
            )

            _forecastMap.value =
                _forecastMap.value.toMutableMap().apply {
                    put(key, response.list)
                }

        } catch (e: Exception) {

            if (cached != null) {

                val list: List<ForecastItem> =
                    Gson().fromJson(
                        cached.forecastJson,
                        object : TypeToken<List<ForecastItem>>() {}.type
                    )

                _forecastMap.value =
                    _forecastMap.value.toMutableMap().apply {
                        put(key, list)
                    }
            }
        }
    }
}
}
