package com.example.skylogic.data.local.weather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey val locationKey: String,

    // flat fields — used by FavoriteItem & FavoriteDetailsScreen
    val name: String,
    val temp: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val description: String,
    val icon: String,

    // full JSON — used by WeatherViewModel (home screen needs complete response)
    val weatherJson: String,

    val timestamp: Long
)


