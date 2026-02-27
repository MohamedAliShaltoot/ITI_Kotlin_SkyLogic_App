package com.example.skylogic.view.settingView.settingViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.skylogic.view.settingView.FakeSettingsDataStore
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeDataStore: FakeSettingsDataStore
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDataStore = FakeSettingsDataStore()
        viewModel = SettingsViewModel(
            application = mockk(relaxed = true),
            dataStore = fakeDataStore
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun defaultValues_areCorrect() = runTest {
        advanceUntilIdle()
        assertNull(viewModel.lat.value)
        assertNull(viewModel.lon.value)
        assertEquals("GPS",        viewModel.locationMode.value)
        assertEquals("Celsius",    viewModel.tempUnit.value)
        assertEquals("meter/sec",  viewModel.windUnit.value)
        assertEquals("English",    viewModel.language.value)
    }

    @Test
    fun setLocationMode_GPS_updatesState() = runTest {
        viewModel.setLocationMode("GPS")
        advanceUntilIdle()
        assertEquals("GPS", viewModel.locationMode.value)
    }


    @Test
    fun setTempUnit_Celsius_updatesState() = runTest {
        viewModel.setTempUnit("Celsius")
        advanceUntilIdle()
        assertEquals("Celsius", viewModel.tempUnit.value)
    }



    @Test
    fun setWindUnit_meterPerSec_updatesState() = runTest {
        viewModel.setWindUnit("meter/sec")
        advanceUntilIdle()
        assertEquals("meter/sec", viewModel.windUnit.value)
    }

    @Test
    fun setLanguage_English_updatesState() = runTest {
        viewModel.setLanguage("English")
        advanceUntilIdle()
        assertEquals("English", viewModel.language.value)
    }



}