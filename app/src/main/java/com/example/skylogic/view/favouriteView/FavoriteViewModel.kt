package com.example.skylogic.view.favouriteView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skylogic.data.local.AppDatabase
import com.example.skylogic.data.local.FavoriteEntity
import com.example.skylogic.data.remote.RetrofitInstance
import com.example.skylogic.data.repository.FavoriteRepository
import com.example.skylogic.models.CurrentWeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application)
    : AndroidViewModel(application) {

    private val dao =
        AppDatabase.getDatabase(application).favoriteDao()

    private val repository = FavoriteRepository(dao)
    private val _weatherMap = MutableStateFlow<Map<String, CurrentWeatherResponse>>(emptyMap())
    val weatherMap: StateFlow<Map<String, CurrentWeatherResponse>> = _weatherMap

    val favorites =
        repository.favorites
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun addFavorite(name: String, lat: Double, lon: Double) {

        viewModelScope.launch {

            repository.addFavorite(
                FavoriteEntity(
                    name = name,
                    lat = lat,
                    lon = lon
                )
            )
        }
    }

    fun deleteFavorite(favorite: FavoriteEntity) {

        viewModelScope.launch {
            repository.removeFavorite(favorite)
        }
    }

    fun loadWeatherForFavorite(lat: Double, lon: Double, key: String) {

        if (_weatherMap.value.containsKey(key)) return

        viewModelScope.launch {
            try {
                val weather = RetrofitInstance.api.getCurrentWeather(lat, lon)

                _weatherMap.value =
                    _weatherMap.value.toMutableMap().apply {
                        put(key, weather)
                    }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
