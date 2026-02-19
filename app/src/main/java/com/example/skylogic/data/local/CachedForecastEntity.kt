package com.example.skylogic.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_forecast")
data class CachedForecastEntity(

    @PrimaryKey
    val locationKey: String,

    val forecastJson: String,

    val timestamp: Long
)
