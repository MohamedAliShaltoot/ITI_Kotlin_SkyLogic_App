//package com.example.skylogic.data.repository
//
//import com.example.skylogic.data.local.LocalDataSource
//import com.example.skylogic.data.local.alert.AlertEntity
//import com.example.skylogic.data.local.fav.FavoriteEntity
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//
//class FakeLocalDataSource : LocalDataSource(
//    context = mockk() // will not be used
//) {
//
//    private val favorites = mutableListOf<FavoriteEntity>()
//    private val alerts = mutableListOf<AlertEntity>()
//    private val favoriteFlow = MutableStateFlow<List<FavoriteEntity>>(emptyList())
//
//    override suspend fun insertFavorite(favorite: FavoriteEntity) {
//        favorites.add(favorite)
//        favoriteFlow.value = favorites
//    }
//
//    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
//        favorites.remove(favorite)
//        favoriteFlow.value = favorites
//    }
//
//    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
//        return favoriteFlow
//    }
//
//    override suspend fun insertAlert(alert: AlertEntity): Long {
//        alerts.add(alert)
//        return 1L
//    }
//}