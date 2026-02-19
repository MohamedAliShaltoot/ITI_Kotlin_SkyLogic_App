package com.example.skylogic.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(

    @PrimaryKey
    val key: String, // lat,lon combination

    val name: String,
    val temp: Double,
    val description: String,
    val icon: String,
    val timestamp: Long // for freshness control
)

