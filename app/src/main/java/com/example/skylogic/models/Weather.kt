package com.example.skylogic.models

data class CurrentWeatherResponse(
    val name: String,
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val visibility: Int,
    val dt: Long,
    val timezone: Int
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int? = null,
    val grnd_level: Int? = null
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

