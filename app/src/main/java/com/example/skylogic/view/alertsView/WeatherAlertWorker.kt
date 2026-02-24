package com.example.skylogic.view.alertsView

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.skylogic.AlarmActivity
import com.example.skylogic.R
import com.example.skylogic.data.local.AppDatabase
import com.example.skylogic.data.remote.RetrofitInstance
import com.example.skylogic.view.settingView.SettingsDataStore
import kotlinx.coroutines.flow.firstOrNull

class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val alertId = inputData.getInt("ALERT_ID", -1)
        if (alertId == -1) return Result.failure()

        val db = AppDatabase.getDatabase(applicationContext)
        val alertDao = db.alertDao()

        val alert = alertDao.getAlertById(alertId)
            ?: return Result.failure()

        val now = System.currentTimeMillis()

        // 1️⃣ Check duration validity
        if (now > alert.endTime) {
            WorkManager.getInstance(applicationContext)
                .cancelUniqueWork("weather_alert_$alertId")
            return Result.success()
        }

        // 2️⃣ Get selected location from Settings
        val settings = SettingsDataStore(applicationContext)

        val lat = settings.lat.firstOrNull() ?: return Result.failure()
        val lon = settings.lon.firstOrNull() ?: return Result.failure()

        try {

            val forecast =
                RetrofitInstance.api.getForecast(lat, lon)

            val match = forecast.list.any { item ->

                when (alert.condition) {

                    "rain" ->
                        item.weather.any { it.main.lowercase() == "rain" }

                    "snow" ->
                        item.weather.any { it.main.lowercase() == "snow" }

                    "temp_high" ->
                        item.main.temp_max > (alert.threshold ?: 0.0)

                    "temp_low" ->
                        item.main.temp_min < (alert.threshold ?: 0.0)

                    "wind" ->
                        item.wind.speed > (alert.threshold ?: 0.0)

                    else -> false
                }
            }

            if (match) {
                if (alert.type == "notification") {
                    showNotification()
                } else {
                    openAlarmScreen()
                }
            }

            return Result.success()

        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun showNotification() {
        val context = applicationContext
        val channelId = "weather_alert_channel"

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.app_icon)
            .setContentTitle("Weather Alert")
            .setContentText("Weather condition matched!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(1001, notification)
    }

    private fun openAlarmScreen() {
        val intent = Intent(
            applicationContext,
            AlarmActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        applicationContext.startActivity(intent)
    }
}

