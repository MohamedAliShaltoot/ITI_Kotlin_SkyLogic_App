package com.example.skylogic.data.repository

import com.example.skylogic.data.remote.IRemoteDataSource
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse

class FakeRemoteDataSource : IRemoteDataSource {

    var fakeWeather: CurrentWeatherResponse? = null
    var fakeForecast: ForecastResponse? = null
    var weatherException: Exception? = null
    var forecastException: Exception? = null

    override suspend fun getCurrentWeather(lat: Double, lon: Double, units: String, lang: String): CurrentWeatherResponse {
        weatherException?.let { throw it }
        return fakeWeather ?: error("fakeWeather not set in test")
    }

    override suspend fun getForecast(lat: Double, lon: Double, units: String, lang: String): ForecastResponse {
        forecastException?.let { throw it }
        return fakeForecast ?: error("fakeForecast not set in test")
    }

    override suspend fun getCityCoordinates(city: String): List<GeoResponse> = emptyList()

    override suspend fun reverseGeocode(lat: Double, lon: Double): List<GeoResponse> = emptyList()
}