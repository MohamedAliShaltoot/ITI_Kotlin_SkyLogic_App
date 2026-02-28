package com.example.skylogic.view.settingView


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

private val Context.dataStore by preferencesDataStore("settings")

fun applyLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.createConfigurationContext(config)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
class SettingsDataStore(private val context: Context) : ISettingsDataStore{

    companion object {
        val LOCATION_MODE = stringPreferencesKey("location_mode")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val LANGUAGE = stringPreferencesKey("language")
        val LAT = doublePreferencesKey("lat")
        val LON = doublePreferencesKey("lon")
    }
    override val lat: Flow<Double?> =
        context.dataStore.data.map { it[LAT] }

    override val lon: Flow<Double?> =
        context.dataStore.data.map { it[LON] }

    override val locationMode: Flow<String> =
        context.dataStore.data.map {
            it[LOCATION_MODE] ?: "GPS"
        }

    override  val tempUnit: Flow<String> =
        context.dataStore.data.map {
            it[TEMP_UNIT] ?: "Celsius"
        }

    override  val windUnit: Flow<String> =
        context.dataStore.data.map {
            it[WIND_UNIT] ?: "meter/sec"
        }

    override  val language: Flow<String> =
        context.dataStore.data.map {
            it[LANGUAGE] ?: "English"
        }

    override  suspend fun saveLocationMode(value: String) {
        context.dataStore.edit {
            it[LOCATION_MODE] = value
        }
    }

    override  suspend fun saveTempUnit(value: String) {
        context.dataStore.edit {
            it[TEMP_UNIT] = value
        }
    }

    override   suspend fun saveWindUnit(value: String) {
        context.dataStore.edit {
            it[WIND_UNIT] = value
        }
    }

    override   suspend fun saveLanguage(value: String) {
        context.dataStore.edit {
            it[LANGUAGE] = value
        }
    }

    override   suspend fun saveCustomLocation(lat: Double, lon: Double) {
        context.dataStore.edit {
            it[LAT] = lat
            it[LON] = lon
        }
    }

}
