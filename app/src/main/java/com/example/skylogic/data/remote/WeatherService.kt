package com.example.skylogic.data.remote

import com.example.skylogic.AppConstants
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.GeoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
      //  @Query("appid") apiKey: String = AppConstants.API_KEY
    ): CurrentWeatherResponse


    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
      //  @Query("appid") apiKey: String = AppConstants.API_KEY
    ): ForecastResponse


    //Direct Search (City name -> lat/lon)
    @GET("geo/1.0/direct")
    suspend fun getCityCoordinates(
        @Query("q") city: String,
        @Query("limit") limit: Int = 5,
       // @Query("appid") apiKey: String = AppConstants.API_KEY
    ): List<GeoResponse>


    //Reverse Geocoding (lat/lon -> city name)
    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
     //   @Query("appid") apiKey: String = AppConstants.API_KEY
    ): List<GeoResponse>
}


