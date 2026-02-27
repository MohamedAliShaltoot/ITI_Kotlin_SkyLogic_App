package com.example.skylogic.view.weatherView.weatherViewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skylogic.data.local.LocalDataSource
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.data.remote.RemoteDataSource
import com.example.skylogic.data.remote.RetrofitInstance
import com.example.skylogic.data.repository.AppRepository
import com.example.skylogic.data.repository.IAppRepository
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.models.GeoResponse
import com.example.skylogic.view.weatherView.WeatherUiState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application ,  private val appRepository: IAppRepository = AppRepository(
    LocalDataSource(application),
    RemoteDataSource()
)) : AndroidViewModel(application) {


    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    val currentWeather: CurrentWeatherResponse?
        get() = when (val s = _uiState.value) {
            is WeatherUiState.Success       -> s.weather
            is WeatherUiState.CachedSuccess -> s.weather
            else                            -> null
        }

    private val gson = Gson()

    suspend fun getCityCoordinates(query: String): List<GeoResponse> =
        appRepository.getCityCoordinates(query)

    suspend fun reverseGeocode(lat: Double, lon: Double): List<GeoResponse> =
        appRepository.reverseGeocode(lat, lon)

    fun getHourlyData(): List<ForecastItem> {
        val forecast = when (val s = _uiState.value) {
            is WeatherUiState.Success       -> s.forecast
            is WeatherUiState.CachedSuccess -> s.forecast
            else                            -> emptyList()
        }
        return forecast.sortedBy { it.dt }.take(8)
    }

    fun getDailyData(): Map<String, List<ForecastItem>> {
        val forecast = when (val s = _uiState.value) {
            is WeatherUiState.Success       -> s.forecast
            is WeatherUiState.CachedSuccess -> s.forecast
            else                            -> emptyList()
        }
        return forecast.groupBy { it.dt_txt.substringBefore(" ") }
    }

    fun fetchWeather(
        lat: Double,
        lon: Double,
        units: String = "metric",
        lang: String = "en",
        locationKey: String = "home"
    ) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {
                val weather  = appRepository.getCurrentWeather(lat, lon, units, lang)
                val forecast = appRepository.getForecast(lat, lon, units, lang).list

                // Cache with ALL fields (flat + JSON)
                appRepository.insertCachedWeather(
                    CachedWeatherEntity(
                        locationKey  = locationKey,
                        name         = weather.name,
                        temp         = weather.main.temp,
                        feelsLike    = weather.main.feels_like,
                        humidity     = weather.main.humidity,
                        pressure     = weather.main.pressure,
                        windSpeed    = weather.wind.speed,
                        description  = weather.weather[0].description,
                        icon         = weather.weather[0].icon,
                        weatherJson  = gson.toJson(weather),   // full JSON for home screen
                        timestamp    = System.currentTimeMillis()
                    )
                )
                appRepository.insertCachedForecast(
                    CachedForecastEntity(
                        locationKey  = locationKey,
                        forecastJson = gson.toJson(forecast),
                        timestamp    = System.currentTimeMillis()
                    )
                )

                _uiState.value = WeatherUiState.Success(weather, forecast)

            } catch (e: Exception) {
                // Network failed → try cache
                val cachedWeather  = appRepository.getCachedWeather(locationKey)
                val cachedForecast = appRepository.getCachedForecast(locationKey)

                if (cachedWeather != null && cachedForecast != null) {
                    val weather = gson.fromJson(
                        cachedWeather.weatherJson,
                        CurrentWeatherResponse::class.java
                    )
                    val forecast: List<ForecastItem> = gson.fromJson(
                        cachedForecast.forecastJson,
                        object : TypeToken<List<ForecastItem>>() {}.type
                    )
                    _uiState.value = WeatherUiState.CachedSuccess(weather, forecast)
                } else {
                    _uiState.value = WeatherUiState.Error(
                        e.message ?: "No internet and no cached data available"
                    )
                }
            }
        }
    }
}



class WeatherViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(
                application = application,
                appRepository = AppRepository(
                    LocalDataSource(application),
                    RemoteDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}