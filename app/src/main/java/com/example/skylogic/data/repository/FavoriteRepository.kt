package com.example.skylogic.data.repository

import com.example.skylogic.data.local.FavoriteDao
import com.example.skylogic.data.local.FavoriteEntity

class FavoriteRepository(private val dao: FavoriteDao) {

    val favorites = dao.getAllFavorites()

    suspend fun addFavorite(favorite: FavoriteEntity) {
        dao.insertFavorite(favorite)
    }

    suspend fun removeFavorite(favorite: FavoriteEntity) {
        dao.deleteFavorite(favorite)
    }
}
