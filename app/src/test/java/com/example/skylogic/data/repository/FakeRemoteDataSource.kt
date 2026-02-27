//package com.example.skylogic.data.repository
//
//import com.example.skylogic.data.remote.RemoteDataSource
//import com.example.skylogic.models.CurrentWeatherResponse
//import com.example.skylogic.models.ForecastResponse
//import com.example.skylogic.models.GeoResponse
//
//class FakeRemoteDataSource : RemoteDataSource {
//
//    var currentWeatherResponse: CurrentWeatherResponse? = null
//
//    override suspend fun getCurrentWeather(
//        lat: Double,
//        lon: Double,
//        units: String,
//        lang: String
//    ): CurrentWeatherResponse {
//        return currentWeatherResponse!!
//    }
//
//    override suspend fun getForecast(
//        lat: Double,
//        lon: Double,
//        units: String,
//        lang: String
//    ): ForecastResponse {
//        throw NotImplementedError()
//    }
//
//    override suspend fun getCityCoordinates(city: String): List<GeoResponse> {
//        return emptyList()
//    }
//
//    override suspend fun reverseGeocode(
//        lat: Double,
//        lon: Double
//    ): List<GeoResponse> {
//        return emptyList()
//    }
//}