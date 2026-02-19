package com.example.skylogic.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        CachedWeatherEntity::class,
        CachedForecastEntity::class
    ],
    version = 3
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun cachedWeatherDao(): CachedWeatherDao
    abstract fun cachedForecastDao(): CachedForecastDao



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
