package com.example.skylogic.data.remote

import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse

class RemoteDataSource(
    private val service: WeatherService = RetrofitInstance.api
) : IRemoteDataSource {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String ,
        lang: String
    ): CurrentWeatherResponse {
        return service.getCurrentWeather(
            lat = lat,
            lon = lon,
            units = units,
            lang = lang
        )
    }

    override  suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String ,
        lang: String
    ): ForecastResponse {
        return service.getForecast(
            lat = lat,
            lon = lon,
            units = units,
            lang = lang
        )
    }

    override   suspend fun getCityCoordinates(city: String): List<GeoResponse> {
        return service.getCityCoordinates(city)
    }

    override suspend fun reverseGeocode(
        lat: Double,
        lon: Double
    ): List<GeoResponse> {
        return service.reverseGeocode(lat, lon)
    }
}