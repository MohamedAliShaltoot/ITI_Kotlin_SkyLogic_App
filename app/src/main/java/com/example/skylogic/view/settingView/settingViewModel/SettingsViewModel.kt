package com.example.skylogic.view.settingView.settingViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skylogic.view.settingView.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) :
    AndroidViewModel(application) {

    private val dataStore = SettingsDataStore(application)
    val lat = dataStore.lat.stateIn(viewModelScope, SharingStarted.Companion.Lazily, null)
    val lon = dataStore.lon.stateIn(viewModelScope, SharingStarted.Companion.Lazily, null)

    fun setCustomLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            dataStore.saveCustomLocation(lat, lon)
        }
    }

    val locationMode = dataStore.locationMode
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, "GPS")

    val tempUnit = dataStore.tempUnit
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, "Celsius")

    val windUnit = dataStore.windUnit
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, "meter/sec")

    val language = dataStore.language
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, "English")

    fun setLocationMode(value: String) {
        viewModelScope.launch {
            dataStore.saveLocationMode(value)
        }
    }

    fun setTempUnit(value: String) {
        viewModelScope.launch {
            dataStore.saveTempUnit(value)
        }
    }

    fun setWindUnit(value: String) {
        viewModelScope.launch {
            dataStore.saveWindUnit(value)
        }
    }

    fun setLanguage(value: String) {
        viewModelScope.launch {
            dataStore.saveLanguage(value)
        }
    }
}