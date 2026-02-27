package com.example.skylogic.view.settingView


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsDataStore : ISettingsDataStore {


    private val _lat          = MutableStateFlow<Double?>(null)
    private val _lon          = MutableStateFlow<Double?>(null)
    private val _locationMode = MutableStateFlow("GPS")
    private val _tempUnit     = MutableStateFlow("Celsius")
    private val _windUnit     = MutableStateFlow("meter/sec")
    private val _language     = MutableStateFlow("English")
    override val lat:          Flow<Double?> = _lat
    override val lon:          Flow<Double?> = _lon
    override val locationMode: Flow<String>  = _locationMode
    override val tempUnit:     Flow<String>  = _tempUnit
    override val windUnit:     Flow<String>  = _windUnit
    override val language:     Flow<String>  = _language

    override suspend fun saveLocationMode(value: String)  { _locationMode.value = value }
    override suspend fun saveTempUnit(value: String)      { _tempUnit.value = value }
    override suspend fun saveWindUnit(value: String)      { _windUnit.value = value }
    override suspend fun saveLanguage(value: String)      { _language.value = value }
    override suspend fun saveCustomLocation(lat: Double, lon: Double) {
        _lat.value = lat
        _lon.value = lon
    }
}