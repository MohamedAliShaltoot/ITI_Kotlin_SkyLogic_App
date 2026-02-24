package com.example.skylogic.data.local.alert

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Insert
    suspend fun insert(alert: AlertEntity) : Long

    @Delete
    suspend fun delete(alert: AlertEntity)

    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<AlertEntity>>
    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: Int): AlertEntity?

}
