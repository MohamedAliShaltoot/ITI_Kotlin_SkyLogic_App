package com.example.skylogic.data.repository

import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.models.City
import com.example.skylogic.models.Clouds
import com.example.skylogic.models.Coord
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.models.ForecastResponse
import com.example.skylogic.models.ForecastSys
import com.example.skylogic.models.Main
import com.example.skylogic.models.Sys
import com.example.skylogic.models.Weather
import com.example.skylogic.models.Wind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppRepositoryTest {

    private lateinit var fakeLocal: FakeLocalDataSource
    private lateinit var fakeRemote: FakeRemoteDataSource
    private lateinit var repository: AppRepository

    private val fakeWeather = CurrentWeatherResponse(
        name = "Cairo",
        coord = Coord(lon = 31.0, lat = 30.0),
        weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
        main = Main(30.0, 32.0, 28.0, 35.0, 1010, 40),
        wind = Wind(5.0, 0),
        sys = Sys("EG", 0L, 0L),
        visibility = 10000,
        dt = 1700000000L,
        timezone = 7200
    )

    private val fakeForecast = ForecastResponse(
        cod = "200", message = 0, cnt = 1,
        list = listOf(
            ForecastItem(
                dt = 1700000000L,
                main = Main(28.0, 30.0, 25.0, 32.0, 1008, 45),
                weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
                clouds = Clouds(10),
                wind = Wind(4.0, 0),
                visibility = 10000,
                pop = 0.0,
                sys = ForecastSys("d"),
                dt_txt = "2024-01-01 12:00:00"
            )
        ),
        city = City(360630, "Cairo", "EG", 7200, 0L, 0L)
    )

    @Before
    fun setup() {
        fakeLocal  = FakeLocalDataSource()
        fakeRemote = FakeRemoteDataSource()
        repository = AppRepository(fakeLocal, fakeRemote)
    }

    // Favorites

    @Test
    fun insertFavorite_delegatesToLocal_andRetrievable() = runTest {
        val favorite = FavoriteEntity(name = "Cairo", lat = 30.0, lon = 31.0)

        repository.insertFavorite(favorite)

        val list = repository.getAllFavorites().first()
        assertEquals(1, list.size)
        assertEquals("Cairo", list[0].name)
    }

    @Test
    fun deleteFavorite_delegatesToLocal_removesItem() = runTest {
        val favorite = FavoriteEntity(name = "Cairo", lat = 30.0, lon = 31.0)
        repository.insertFavorite(favorite)

        val inserted = repository.getAllFavorites().first()[0]
        repository.deleteFavorite(inserted)

        assertEquals(0, repository.getAllFavorites().first().size)
    }

    // Alerts

    @Test
    fun insertAlert_delegatesToLocal_andGetById() = runTest {
        val alert = AlertEntity(startTime = 1000L, endTime = 2000L, type = "alarm", condition = "rain")

        val id = repository.insertAlert(alert)
        val result = repository.getAlertById(id.toInt())

        assertNotNull(result)
        assertEquals("alarm", result?.type)
        assertEquals("rain",  result?.condition)
    }

    @Test
    fun deleteAlert_delegatesToLocal_removesItem() = runTest {
        val id = repository.insertAlert(
            AlertEntity(startTime = 1000L, endTime = 2000L, type = "notification", condition = "snow")
        )
        val inserted = repository.getAlertById(id.toInt())!!
        repository.deleteAlert(inserted)

        assertNull(repository.getAlertById(id.toInt()))
    }

    @Test
    fun getAllAlerts_delegatesToLocal_returnsAll() = runTest {
        repository.insertAlert(AlertEntity(startTime = 1000L, endTime = 2000L, type = "alarm",        condition = "rain"))
        repository.insertAlert(AlertEntity(startTime = 3000L, endTime = 4000L, type = "notification", condition = "wind", threshold = 20.0))

        val list = repository.getAllAlerts().first()
        assertEquals(2, list.size)
    }

    @Test
    fun getAlertById_nonExistent_returnsNull() = runTest {
        assertNull(repository.getAlertById(999))
    }

    //Cached Weather

    @Test
    fun insertCachedWeather_delegatesToLocal_andRetrievable() = runTest {
        val weather = CachedWeatherEntity(
            locationKey = "home", name = "Cairo", temp = 30.0, feelsLike = 32.0,
            humidity = 40, pressure = 1010, windSpeed = 5.0, description = "clear sky",
            icon = "01d", weatherJson = "{}", timestamp = System.currentTimeMillis()
        )

        repository.insertCachedWeather(weather)

        val result = repository.getCachedWeather("home")
        assertEquals("Cairo", result?.name)
    }

    @Test
    fun getCachedWeather_notInserted_returnsNull() = runTest {
        assertNull(repository.getCachedWeather("home"))
    }

    //Cached Forecast

    @Test
    fun insertCachedForecast_delegatesToLocal_andRetrievable() = runTest {
        val forecast = CachedForecastEntity(
            locationKey = "home",
            forecastJson = "[{\"dt\":1700000000}]",
            timestamp = System.currentTimeMillis()
        )

        repository.insertCachedForecast(forecast)

        val result = repository.getCachedForecast("home")
        assertNotNull(result)
        assertEquals("[{\"dt\":1700000000}]", result?.forecastJson)
    }

    // Remote: getCurrentWeather

    @Test
    fun getCurrentWeather_delegatesToRemote_returnsWeather() = runTest {
        fakeRemote.fakeWeather = fakeWeather

        val result = repository.getCurrentWeather(30.0, 31.0, "metric", "en")

        assertEquals("Cairo", result.name)
        assertEquals(30.0, result.main.temp, 0.0)
    }

    @Test
    fun getCurrentWeather_remoteThrows_exceptionPropagates() = runTest {
        fakeRemote.weatherException = Exception("No internet")

        try {
            repository.getCurrentWeather(30.0, 31.0, "metric", "en")
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assertEquals("No internet", e.message)
        }
    }

    // Remote: getForecast

    @Test
    fun getForecast_delegatesToRemote_returnsForecast() = runTest {
        fakeRemote.fakeForecast = fakeForecast

        val result = repository.getForecast(30.0, 31.0, "metric", "en")

        assertEquals("Cairo", result.city.name)
        assertEquals(1, result.list.size)
    }

    @Test
    fun getForecast_remoteThrows_exceptionPropagates() = runTest {
        fakeRemote.forecastException = Exception("Timeout")

        try {
            repository.getForecast(30.0, 31.0, "metric", "en")
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assertEquals("Timeout", e.message)
        }
    }
}