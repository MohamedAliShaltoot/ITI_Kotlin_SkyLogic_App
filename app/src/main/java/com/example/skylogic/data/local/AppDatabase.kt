package com.example.skylogic.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skylogic.data.local.alert.AlertDao
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteDao
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastDao
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherDao
import com.example.skylogic.data.local.weather.CachedWeatherEntity

@Database(
    entities = [
        FavoriteEntity::class,
        CachedWeatherEntity::class,
        CachedForecastEntity::class,
        AlertEntity::class
    ],
    version = 6
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun cachedWeatherDao(): CachedWeatherDao
    abstract fun cachedForecastDao(): CachedForecastDao
    abstract fun alertDao(): AlertDao


    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}
