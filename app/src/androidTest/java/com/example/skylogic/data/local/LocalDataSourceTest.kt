package com.example.skylogic.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = LocalDataSource(context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    // 1 Insert & Retrieve Favorite
    @Test
    fun insertFavorite_andRetrieve() = runBlocking {
        val favorite = FavoriteEntity(
            name = "Cairo",
            lat = 30.0,
            lon = 31.0
        )

        localDataSource.insertFavorite(favorite)

        val list = localDataSource.getAllFavorites().first()

        assertEquals(1, list.size)
        assertEquals("Cairo", list[0].name)
    }

    // 2 Insert & Retrieve CachedWeather
    @Test
    fun insertCachedWeather_andGetWeather() = runBlocking {
        val weather = CachedWeatherEntity(
            locationKey = "123",
            name = "Cairo",
            temp = 30.0,
            feelsLike = 32.0,
            humidity = 60,
            pressure = 1010,
            windSpeed = 5.0,
            description = "Sunny",
            icon = "01d",
            weatherJson = "{}",
            timestamp = System.currentTimeMillis()
        )

        localDataSource.insertCachedWeather(weather)

        val result = localDataSource.getCachedWeather("123")

        assertEquals("Cairo", result?.name)
        assertEquals(30.0, result?.temp)
    }

    // 3 Insert & Retrieve Alert
    @Test
    fun insertAlert_andGetById() = runBlocking {
        val alert = AlertEntity(
            startTime = 1000L,
            endTime = 2000L,
            type = "alarm",
            condition = "wind",
            threshold = 20.0
        )

        val id = localDataSource.insertAlert(alert)

        val result = localDataSource.getAlertById(id.toInt())

        assertEquals("alarm", result?.type)
    }
}