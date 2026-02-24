package com.example.skylogic.view.alertsView

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.skylogic.AlertReceiver
import com.example.skylogic.data.local.AlertEntity
import com.example.skylogic.data.local.AppDatabase
import com.example.skylogic.data.repository.AlertRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

class AlertViewModel(application: Application)
    : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).alertDao()
    private val repository = AlertRepository(dao)
    private val scheduler = AlarmScheduler(application)
    val alerts = repository.alerts
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun addAlert(
        start: Long,
        end: Long,
        type: String,
        condition: String,
        threshold: Double?
    ) {

        viewModelScope.launch {

            val id = repository.insert(
                AlertEntity(
                    startTime = start,
                    endTime = end,
                    type = type,
                    condition = condition,
                    threshold = threshold
                )
            )

            val alertId = id.toInt()

            val workRequest =
                PeriodicWorkRequestBuilder<WeatherAlertWorker>(
                    15, TimeUnit.MINUTES
                )
                    .setInputData(
                        workDataOf("ALERT_ID" to alertId)
                    )
                    .build()

            WorkManager.getInstance(getApplication())
                .enqueueUniquePeriodicWork(
                    "weather_alert_$alertId",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }


    fun deleteAlert(alert: AlertEntity) {

        viewModelScope.launch {
            repository.delete(alert)

            WorkManager.getInstance(getApplication())
                .cancelUniqueWork("weather_alert_${alert.id}")
        }
    }


    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)

    private fun scheduleAlarm(alert: AlertEntity) {

        val context = getApplication<Application>()

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE)
                    as AlarmManager

        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("type", alert.type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id, // unique requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alert.startTime,
            pendingIntent
        )
    }


    private fun cancelAlarm(alert: AlertEntity) {

        val context = getApplication<Application>()

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlertReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
//    private fun scheduleAlarm(alert: AlertEntity) {
//
//        val context = getApplication<Application>()
//
//        val alarmManager =
//            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        val intent = Intent(context, AlertReceiver::class.java).apply {
//            putExtra("type", alert.type)
//        }
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            alert.id,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            alert.startTime,
//            pendingIntent
//        )
//    }