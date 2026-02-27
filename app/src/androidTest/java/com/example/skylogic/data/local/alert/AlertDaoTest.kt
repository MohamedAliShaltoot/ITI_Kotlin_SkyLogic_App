package com.example.skylogic.data.local.alert

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.runner.AndroidJUnit4
import com.example.skylogic.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var alertDao: AlertDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        alertDao = database.alertDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Test 1: Insert Alert
    @Test
    fun insertAlert_returnsPositiveId() = runBlocking {
        val alert = AlertEntity(
            startTime = 1000L,
            endTime = 2000L,
            type = "notification",
            condition = "rain",
            threshold = null
        )

        val id = alertDao.insert(alert)

        assertTrue(id > 0)
    }

    //Test 2: Get Alert By Id
    @Test
    fun getAlertById_returnsCorrectAlert() = runBlocking {
        val alert = AlertEntity(
            startTime = 1000L,
            endTime = 2000L,
            type = "alarm",
            condition = "wind",
            threshold = 30.0
        )

        val id = alertDao.insert(alert)

        val result = alertDao.getAlertById(id.toInt())

        assertEquals("alarm", result?.type)
        assertEquals("wind", result?.condition)
        assertEquals(30.0, result?.threshold)
    }

    // Test 3: Delete Alert
    @Test
    fun deleteAlert_removesAlertFromDatabase() = runBlocking {
        val alert = AlertEntity(
            startTime = 1000L,
            endTime = 2000L,
            type = "notification",
            condition = "snow"
        )

        val id = alertDao.insert(alert)

        alertDao.delete(alert.copy(id = id.toInt()))

        val result = alertDao.getAlertById(id.toInt())

        assertNull(result)
    }
}