package com.example.skylogic


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) :
    AndroidViewModel(application) {

    private val dataStore = SettingsDataStore(application)
    val lat = dataStore.lat.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val lon = dataStore.lon.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setCustomLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            dataStore.saveCustomLocation(lat, lon)
        }
    }

    val locationMode = dataStore.locationMode
        .stateIn(viewModelScope, SharingStarted.Lazily, "GPS")

    val tempUnit = dataStore.tempUnit
        .stateIn(viewModelScope, SharingStarted.Lazily, "Celsius")

    val windUnit = dataStore.windUnit
        .stateIn(viewModelScope, SharingStarted.Lazily, "meter/sec")

    val language = dataStore.language
        .stateIn(viewModelScope, SharingStarted.Lazily, "English")

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
