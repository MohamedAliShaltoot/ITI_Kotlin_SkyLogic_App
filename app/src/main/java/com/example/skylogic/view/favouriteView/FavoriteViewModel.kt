package com.example.skylogic.view.favouriteView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skylogic.data.local.AppDatabase
import com.example.skylogic.data.local.FavoriteEntity
import com.example.skylogic.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application)
    : AndroidViewModel(application) {

    private val dao =
        AppDatabase.getDatabase(application).favoriteDao()

    private val repository = FavoriteRepository(dao)

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
}
