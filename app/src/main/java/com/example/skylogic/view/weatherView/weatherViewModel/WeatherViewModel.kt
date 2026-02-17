package com.example.skylogic.view.weatherView.weatherViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skylogic.data.remote.RetrofitInstance
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.models.GeoResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    var currentWeather by mutableStateOf<CurrentWeatherResponse?>(null)
        private set

    var forecast by mutableStateOf<List<ForecastItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set
    private val weatherService = RetrofitInstance.api

    suspend fun getCityCoordinates(query: String): List<GeoResponse> {
        return weatherService.getCityCoordinates(query)
    }

    suspend fun reverseGeocode(lat: Double, lon: Double): List<GeoResponse> {
        return weatherService.reverseGeocode(lat, lon)
    }

    fun fetchWeather(
    lat: Double,
    lon: Double,
    units: String = "metric",
    lang: String = "en"
) {

    viewModelScope.launch {
        try {
            isLoading = true

            currentWeather =
                RetrofitInstance.api.getCurrentWeather(
                    lat,
                    lon,
                    units,
                    lang
                )

            forecast =
                RetrofitInstance.api.getForecast(
                    lat,
                    lon,
                    units,
                    lang
                ).list

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
}

    fun getHourlyData(): List<ForecastItem> {
        return forecast
            .sortedBy { it.dt }
            .take(8)
    }

    // Group by day for daily forecast
    fun getDailyData(): Map<String, List<ForecastItem>> {
        return forecast.groupBy {
            it.dt_txt.substringBefore(" ")
        }
    }

}