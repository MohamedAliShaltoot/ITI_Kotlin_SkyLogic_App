package com.example.skylogic.data.repository

import com.example.skylogic.data.local.AlertDao
import com.example.skylogic.data.local.AlertEntity

class AlertRepository(private val dao: AlertDao) {

    val alerts = dao.getAllAlerts()

    suspend fun insert(alert: AlertEntity) : Long {
      return  dao.insert(alert)
    }

    suspend fun delete(alert: AlertEntity) {
        dao.delete(alert)
    }
}
