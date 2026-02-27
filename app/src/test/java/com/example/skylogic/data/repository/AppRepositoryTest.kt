package com.example.skylogic.data.repository

import com.example.skylogic.data.local.LocalDataSource
import com.example.skylogic.data.local.fav.FavoriteEntity
import com.example.skylogic.data.remote.RemoteDataSource
import com.example.skylogic.models.Coord
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.Main
import com.example.skylogic.models.Sys
import com.example.skylogic.models.Weather
import com.example.skylogic.models.Wind
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class AppRepositoryTest {

    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repository: AppRepository

    @Before
    fun setup() {
        localDataSource = mock()
        remoteDataSource = mock()
        repository = AppRepository(localDataSource, remoteDataSource)
    }

    // 1 Test insertFavorite calls LocalDataSource
    @Test
    fun insertFavorite_callsLocalDataSource() = runTest {
        val favorite = FavoriteEntity(
            name = "Cairo",
            lat = 30.0,
            lon = 31.0
        )

        repository.insertFavorite(favorite)

        verify(localDataSource).insertFavorite(favorite)
    }

    // 2 Test getAllFavorites returns flow from Local
    @Test
    fun getAllFavorites_returnsFlowFromLocal() = runTest {
        val fakeList = listOf(
            FavoriteEntity(name = "Cairo", lat = 30.0, lon = 31.0)
        )

        whenever(localDataSource.getAllFavorites())
            .thenReturn(flowOf(fakeList))

        val result = repository.getAllFavorites().first()

        assertEquals(1, result.size)
        assertEquals("Cairo", result[0].name)
    }

    // 3 Test getCurrentWeather returns Remote result
    @Test
    fun getCurrentWeather_returnsRemoteData() = runTest {

        val fakeResponse = CurrentWeatherResponse(
            name = "Cairo",
            coord = Coord(31.0, 30.0),
            weather = listOf(
                Weather(800, "Clear", "clear sky", "01d")
            ),
            main = Main(
                temp = 30.0,
                feels_like = 32.0,
                temp_min = 28.0,
                temp_max = 31.0,
                pressure = 1010,
                humidity = 60
            ),
            wind = Wind(
                speed = 5.0,
                deg = 180
            ),
            sys = Sys(
                country = "EG",
                sunrise = 1000L,
                sunset = 2000L
            ),
            visibility = 10000,
            dt = 123456L,
            timezone = 7200
        )

        whenever(
            remoteDataSource.getCurrentWeather(30.0, 31.0, "metric", "en")
        ).thenReturn(fakeResponse)

        val result = repository.getCurrentWeather(30.0, 31.0)

        assertEquals("Cairo", result.name)
        verify(remoteDataSource)
            .getCurrentWeather(30.0, 31.0, "metric", "en")
    }
}