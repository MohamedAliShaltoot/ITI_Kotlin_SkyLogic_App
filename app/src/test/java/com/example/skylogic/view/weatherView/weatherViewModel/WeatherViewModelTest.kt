package com.example.skylogic.view.weatherView.weatherViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.skylogic.data.local.forcast.CachedForecastEntity
import com.example.skylogic.data.local.weather.CachedWeatherEntity
import com.example.skylogic.data.repository.FakeAppRepository
import com.example.skylogic.view.weatherView.WeatherUiState
import com.google.gson.Gson
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepo: FakeAppRepository
    private lateinit var viewModel: WeatherViewModel


    private val fakeWeather = CurrentWeatherResponse(
        name = "Cairo",
        coord = Coord(lon = 31.0, lat = 30.0),
        weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
        main = Main(temp = 30.0, feels_like = 32.0, temp_min = 28.0, temp_max = 35.0, pressure = 1010, humidity = 40),
        wind = Wind(speed = 5.0, deg = 0),
        sys = Sys(country = "EG", sunrise = 0L, sunset = 0L),
        visibility = 10000,
        dt = 1700000000L,
        timezone = 7200
    )

    private val fakeForecastResponse = ForecastResponse(
        cod = "200",
        message = 0,
        cnt = 1,
        list = listOf(
            ForecastItem(
                dt = 1700000000L,
                main = Main(temp = 28.0, feels_like = 30.0, temp_min = 25.0, temp_max = 32.0, pressure = 1008, humidity = 45),
                weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
                clouds = Clouds(all = 10),
                wind = Wind(speed = 4.0, deg = 0),
                visibility = 10000,
                pop = 0.0,
                sys = ForecastSys(pod = "d"),
                dt_txt = "2024-01-01 12:00:00"
            )
        ),
        city = City(id = 360630, name = "Cairo", country = "EG", timezone = 7200, sunrise = 0L, sunset = 0L)
    )


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeAppRepository()
        viewModel = WeatherViewModel(
            application = mockk(relaxed = true),
            appRepository = fakeRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun fetchWeather_success_uiStateBecomesSuccess() = runTest {
        fakeRepo.fakeWeather = fakeWeather
        fakeRepo.fakeForecastResponse = fakeForecastResponse

        viewModel.fetchWeather(lat = 30.0, lon = 31.0)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Success but got $state", state is WeatherUiState.Success)
        val success = state as WeatherUiState.Success
        assertEquals("Cairo", success.weather.name)
        assertEquals(1, success.forecast.size)
    }

    @Test
    fun fetchWeather_networkFailsWithCache_uiStateBecomesCachedSuccess() = runTest {
        fakeRepo.weatherException = Exception("No internet")

        val gson = Gson()
        fakeRepo.fakeCachedWeather = CachedWeatherEntity(
            locationKey = "home",
            name = "Cairo",
            temp = 30.0,
            feelsLike = 32.0,
            humidity = 40,
            pressure = 1010,
            windSpeed = 5.0,
            description = "clear sky",
            icon = "01d",
            weatherJson = gson.toJson(fakeWeather),
            timestamp = System.currentTimeMillis()
        )
        fakeRepo.fakeCachedForecast = CachedForecastEntity(
            locationKey = "home",
            forecastJson = gson.toJson(fakeForecastResponse.list),
            timestamp = System.currentTimeMillis()
        )

        viewModel.fetchWeather(lat = 30.0, lon = 31.0)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected CachedSuccess but got $state", state is WeatherUiState.CachedSuccess)
        assertEquals("Cairo", (state as WeatherUiState.CachedSuccess).weather.name)
    }

    @Test
    fun fetchWeather_networkFailsNoCache_uiStateBecomesError() = runTest {
        fakeRepo.weatherException = Exception("Timeout")

        viewModel.fetchWeather(lat = 30.0, lon = 31.0)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Error but got $state", state is WeatherUiState.Error)
        assertEquals("Timeout", (state as WeatherUiState.Error).message)
    }

    @Test
    fun fetchWeather_emitsLoadingFirst() = runTest {
        fakeRepo.fakeWeather = fakeWeather
        fakeRepo.fakeForecastResponse = fakeForecastResponse

        viewModel.uiState.test {
            viewModel.fetchWeather(lat = 30.0, lon = 31.0)

            assertEquals(WeatherUiState.Loading, awaitItem())
            assertTrue(awaitItem() is WeatherUiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getHourlyData_returnsMax8ItemsSortedByDt() = runTest {
        val items = (1..10).map { i ->
            ForecastItem(
                dt = i.toLong(),
                main = Main(28.0, 30.0, 25.0, 32.0, 1008, 45),
                weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
                clouds = Clouds(all = 10),
                wind = Wind(speed = 4.0, deg = 0),
                visibility = 10000,
                pop = 0.0,
                sys = ForecastSys(pod = "d"),
                dt_txt = "2024-01-01 12:00:00"
            )
        }
        fakeRepo.fakeWeather = fakeWeather
        fakeRepo.fakeForecastResponse = fakeForecastResponse.copy(list = items)

        viewModel.fetchWeather(lat = 30.0, lon = 31.0)
        advanceUntilIdle()

        val hourly = viewModel.getHourlyData()

        assertEquals(8, hourly.size)
        assertEquals(1L, hourly.first().dt)
        assertEquals(8L, hourly.last().dt)
    }

    @Test
    fun getDailyData_groupsForecastItemsByDate() = runTest {
        val items = listOf(
            ForecastItem(1L, Main(25.0,27.0,22.0,28.0,1005,50), listOf(Weather(800,"Clear","clear sky","01d")), Clouds(5),  Wind(3.0,0), 10000, 0.0, ForecastSys("d"), "2024-01-01 06:00:00"),
            ForecastItem(2L, Main(30.0,32.0,28.0,34.0,1008,40), listOf(Weather(800,"Clear","clear sky","01d")), Clouds(0),  Wind(4.0,0), 10000, 0.0, ForecastSys("d"), "2024-01-01 12:00:00"),
            ForecastItem(3L, Main(20.0,22.0,18.0,24.0,1010,60), listOf(Weather(800,"Clear","clear sky","01d")), Clouds(20), Wind(2.0,0), 10000, 0.0, ForecastSys("n"), "2024-01-02 06:00:00"),
        )
        fakeRepo.fakeWeather = fakeWeather
        fakeRepo.fakeForecastResponse = fakeForecastResponse.copy(list = items)

        viewModel.fetchWeather(lat = 30.0, lon = 31.0)
        advanceUntilIdle()

        val daily = viewModel.getDailyData()

        assertEquals(2, daily.keys.size)
        assertEquals(2, daily["2024-01-01"]?.size)
        assertEquals(1, daily["2024-01-02"]?.size)
    }

    @Test
    fun currentWeather_returnsWeatherWhenStateIsSuccess() = runTest {
        fakeRepo.fakeWeather = fakeWeather
        fakeRepo.fakeForecastResponse = fakeForecastResponse

        viewModel.fetchWeather(lat = 30.0, lon = 31.0)
        advanceUntilIdle()

        assertNotNull(viewModel.currentWeather)
        assertEquals("Cairo", viewModel.currentWeather?.name)
    }

    @Test
    fun currentWeather_returnsNullWhenStateIsLoading() {
        assertNull(viewModel.currentWeather)
    }
}