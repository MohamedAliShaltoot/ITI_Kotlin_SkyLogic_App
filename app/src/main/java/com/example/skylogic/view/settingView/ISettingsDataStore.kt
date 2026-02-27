package com.example.skylogic.view.settingView


import kotlinx.coroutines.flow.Flow

interface ISettingsDataStore {
    val lat: Flow<Double?>
    val lon: Flow<Double?>
    val locationMode: Flow<String>
    val tempUnit: Flow<String>
    val windUnit: Flow<String>
    val language: Flow<String>

    suspend fun saveLocationMode(value: String)
    suspend fun saveTempUnit(value: String)
    suspend fun saveWindUnit(value: String)
    suspend fun saveLanguage(value: String)
    suspend fun saveCustomLocation(lat: Double, lon: Double)
}