package com.example.skylogic.data.remote


import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse

interface IRemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String = "metric", lang: String = "en"): CurrentWeatherResponse
    suspend fun getForecast(lat: Double, lon: Double, units: String = "metric", lang: String = "en"): ForecastResponse
    suspend fun getCityCoordinates(city: String): List<GeoResponse>
    suspend fun reverseGeocode(lat: Double, lon: Double): List<GeoResponse>
}