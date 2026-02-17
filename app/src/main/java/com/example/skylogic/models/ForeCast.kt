package com.example.skylogic.models

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)
data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: ForecastSys,
    val dt_txt: String
)

data class Clouds(
    val all: Int
)
data class ForecastSys(
    val pod: String   // "d" or "n"
)


