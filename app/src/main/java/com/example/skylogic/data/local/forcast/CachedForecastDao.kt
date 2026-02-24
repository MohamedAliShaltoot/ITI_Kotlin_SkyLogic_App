package com.example.skylogic.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(forecast: CachedForecastEntity)

    @Query("SELECT * FROM cached_forecast WHERE locationKey = :key")
    suspend fun getForecast(key: String): CachedForecastEntity?
}
