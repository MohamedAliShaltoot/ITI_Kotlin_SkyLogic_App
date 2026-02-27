package com.example.skylogic.data.local.fav

import org.junit.runner.RunWith
import com.example.skylogic.data.local.AppDatabase
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test



@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var favoriteDao: FavoriteDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        favoriteDao = database.favoriteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // 1 Insert Favorite
    @Test
    fun insertFavorite_addsItem() = runBlocking {
        val favorite = FavoriteEntity(
            name = "Cairo",
            lat = 30.0,
            lon = 31.0
        )

        favoriteDao.insertFavorite(favorite)

        val list = favoriteDao.getAllFavorites().first()

        assertEquals(1, list.size)
        assertEquals("Cairo", list[0].name)
    }

    // 2 Delete Favorite
    @Test
    fun deleteFavorite_removesItem() = runBlocking {

        val favorite = FavoriteEntity(
            name = "Alex",
            lat = 31.0,
            lon = 29.0
        )

        // Insert and capture generated ID
        favoriteDao.insertFavorite(favorite)

        val inserted = favoriteDao.getAllFavorites().first().first()

        // Delete using the entity with correct ID
        favoriteDao.deleteFavorite(inserted)

        val list = favoriteDao.getAllFavorites().first()

        assertTrue(list.isEmpty())
    }

    // 3 Insert Multiple Favorites
    @Test
    fun insertMultipleFavorites_returnsCorrectCount() = runBlocking {
        val fav1 = FavoriteEntity(name = "Cairo", lat = 30.0, lon = 31.0)
        val fav2 = FavoriteEntity(name = "Giza", lat = 29.9, lon = 31.2)

        favoriteDao.insertFavorite(fav1)
        favoriteDao.insertFavorite(fav2)

        val list = favoriteDao.getAllFavorites().first()

        assertEquals(2, list.size)
    }
}