package com.example.skylogic.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: CachedWeatherEntity)

    @Query("SELECT * FROM cached_weather WHERE locationKey = :locationKey")
    suspend fun getWeather(locationKey: String): CachedWeatherEntity?

}
