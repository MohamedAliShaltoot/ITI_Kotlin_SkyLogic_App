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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val observer = ConnectivityObserver(application)

    val networkState: StateFlow<NetworkState> =
        observer.observe()
            .stateIn(viewModelScope, SharingStarted.Eagerly, NetworkState.Available)

    private val remote = RemoteDataSource()
    private val local = LocalDataSource(application)
    private val appRepository = AppRepository(local, remote)

    // Fav List State
    private val _favListState = MutableStateFlow<FavListUiState>(FavListUiState.Loading)
    val favListState: StateFlow<FavListUiState> = _favListState

    // Fav Details State
    private val _favDetailsState = MutableStateFlow<FavDetailsUiState>(FavDetailsUiState.Loading)
    val favDetailsState: StateFlow<FavDetailsUiState> = _favDetailsState

    private val _weatherMap = MutableStateFlow<Map<String, CachedWeatherEntity>>(emptyMap())
    val weatherMap: StateFlow<Map<String, CachedWeatherEntity>> = _weatherMap

    //Favorites list
    init {
        viewModelScope.launch {
            appRepository.getAllFavorites().collect { list ->
                _favListState.value = when {
                    list.isEmpty() -> FavListUiState.Empty
                    else -> FavListUiState.Success(list)
                }
            }
        }
    }

    fun addFavorite(name: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            appRepository.insertFavorite(FavoriteEntity(name = name, lat = lat, lon = lon))
        }
    }

    fun deleteFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            appRepository.deleteFavorite(favorite)
        }
    }

    // Weather for list cards
    fun loadWeatherForFavorite(lat: Double, lon: Double) {
        viewModelScope.launch {
            val key = "$lat,$lon"
            val cached = appRepository.getCachedWeather(key)
            val isOnline = networkState.value is NetworkState.Available

            if (!isOnline) {
                cached?.let { _weatherMap.update { map -> map + (key to it) } }
                return@launch
            }

            try {
                val response = appRepository.getCurrentWeather(lat, lon,units = "metric", lang = "en")
                val entity = CachedWeatherEntity(
                    locationKey = key,
                    name = response.name,
                    temp = response.main.temp,
                    feelsLike = response.main.feels_like,
                    humidity = response.main.humidity,
                    pressure = response.main.pressure,
                    windSpeed = response.wind.speed,
                    description = response.weather[0].description,
                    icon = response.weather[0].icon,
                    weatherJson = Gson().toJson(response),
                    timestamp = System.currentTimeMillis()
                )
                appRepository.insertCachedWeather(entity)
                _weatherMap.update { map -> map + (key to entity) }
            } catch (e: Exception) {
                cached?.let { _weatherMap.update { map -> map + (key to it) } }
            }
        }
    }

    // Details state loader
    fun loadDetailsFor(lat: Double, lon: Double) {
        val key = "$lat,$lon"
        _favDetailsState.value = FavDetailsUiState.Loading

        viewModelScope.launch {
            val cachedWeather = appRepository.getCachedWeather(key)
            val cachedForecast = appRepository.getCachedForecast(key)
            val isOnline = networkState.value is NetworkState.Available

            // Offline path
            if (!isOnline) {
                if (cachedWeather != null && cachedForecast != null) {
                    val forecastList: List<ForecastItem> = Gson().fromJson(
                        cachedForecast.forecastJson,
                        object : TypeToken<List<ForecastItem>>() {}.type
                    )
                    _favDetailsState.value = FavDetailsUiState.Success(cachedWeather, forecastList)
                } else {
                    _favDetailsState.value = FavDetailsUiState.NoCache
                }
                return@launch
            }

            // Online path
            try {
                val weatherResponse = appRepository.getCurrentWeather(lat, lon)
                val weatherEntity = CachedWeatherEntity(
                    locationKey = key,
                    name = weatherResponse.name,
                    temp = weatherResponse.main.temp,
                    feelsLike = weatherResponse.main.feels_like,
                    humidity = weatherResponse.main.humidity,
                    pressure = weatherResponse.main.pressure,
                    windSpeed = weatherResponse.wind.speed,
                    description = weatherResponse.weather[0].description,
                    icon = weatherResponse.weather[0].icon,
                    weatherJson = Gson().toJson(weatherResponse),
                    timestamp = System.currentTimeMillis()
                )
                appRepository.insertCachedWeather(weatherEntity)

                val now = System.currentTimeMillis()
                val isFresh = cachedForecast != null && (now - cachedForecast.timestamp) < 30 * 60 * 1000

                val forecastList: List<ForecastItem> = if (isFresh) {
                    Gson().fromJson(
                        cachedForecast!!.forecastJson,
                        object : TypeToken<List<ForecastItem>>() {}.type
                    )
                } else {
                    val forecastResponse = appRepository.getForecast(lat, lon)
                    val json = Gson().toJson(forecastResponse.list)
                    appRepository.insertCachedForecast(
                        CachedForecastEntity(locationKey = key, forecastJson = json, timestamp = now)
                    )
                    forecastResponse.list
                }

                _favDetailsState.value = FavDetailsUiState.Success(weatherEntity, forecastList)

            } catch (e: Exception) {
                if (cachedWeather != null && cachedForecast != null) {
                    val forecastList: List<ForecastItem> = Gson().fromJson(
                        cachedForecast.forecastJson,
                        object : TypeToken<List<ForecastItem>>() {}.type
                    )
                    _favDetailsState.value = FavDetailsUiState.Success(cachedWeather, forecastList)
                } else {
                    _favDetailsState.value = FavDetailsUiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }
}
