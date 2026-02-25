package com.example.skylogic.view.alertsView

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.skylogic.AlarmActivity
import com.example.skylogic.MainActivity
import com.example.skylogic.R
import com.example.skylogic.data.local.AppDatabase
import com.example.skylogic.data.local.LocalDataSource
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.remote.RemoteDataSource
import com.example.skylogic.data.remote.RetrofitInstance
import com.example.skylogic.data.repository.AppRepository
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.view.settingView.SettingsDataStore
import kotlinx.coroutines.flow.firstOrNull

class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val alertId = inputData.getInt("ALERT_ID", -1)
        if (alertId == -1) return Result.failure()
        val remote = RemoteDataSource()
         val local = LocalDataSource(applicationContext)
         val appRepository = AppRepository(local ,remote)

        val alert = appRepository.getAlertById(alertId)
            ?: return Result.failure()

        val now = System.currentTimeMillis()

        // Check duration validity
        if (now > alert.endTime) {
            WorkManager.getInstance(applicationContext)
                .cancelUniqueWork("weather_alert_$alertId")
            return Result.success()
        }

        // Get selected location from Settings
        val settings = SettingsDataStore(applicationContext)

        val lat = settings.lat.firstOrNull() ?: return Result.failure()
        val lon = settings.lon.firstOrNull() ?: return Result.failure()

        try {

            val forecast =
                appRepository.getForecast(lat, lon)
            val cityName = "${forecast.city.name}, ${forecast.city.country}"
            val matchedItem = forecast.list.firstOrNull { item ->
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

            if (matchedItem != null) {
                if (alert.type == "notification") {
                    showNotification(alert, matchedItem, cityName)
                } else {
                    openAlarmScreen(
                        alert,
                        matchedItem,
                        cityName
                    )
                }
            }

            return Result.success()

        } catch (e: Exception) {
            return Result.retry()
        }
    }
    private fun showNotification(
        alert: AlertEntity,
        matchedItem: ForecastItem,
        cityName: String
    ) {

        val context = applicationContext
        val channelId = "weather_alert_channel"

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        // Open Alerts screen when clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("DESTINATION", "alerts")
            putExtra("ALERT_ID", alert.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val title = when (alert.condition) {
            "rain" -> "🌧 Rain Alert"
            "snow" -> "❄ Snow Alert"
            "wind" -> "🌬 Wind Alert"
            "temp_high" -> "🌡 High Temperature Alert"
            "temp_low" -> "🥶 Low Temperature Alert"
            else -> "Weather Alert"
        }

        val conditionText =
            matchedItem.weather.firstOrNull()?.description
                ?.replaceFirstChar { it.uppercase() }
                ?: "Unknown"

        val description =
            "📍 $cityName\n" +
                    "Condition: $conditionText\n" +
                    "Temp: ${matchedItem.main.temp}°C"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.app_icon)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(description)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(alert.id, notification)
    }

private fun openAlarmScreen(
    alert: AlertEntity,
    matchedItem: ForecastItem,
    cityName: String
) {
    val intent = Intent(
        applicationContext,
        AlarmActivity::class.java
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra("TITLE", alert.condition)
        putExtra("CITY", cityName)
        putExtra("TEMP", matchedItem.main.temp)
        putExtra(
            "DESCRIPTION",
            matchedItem.weather.firstOrNull()?.description ?: ""
        )
    }

    applicationContext.startActivity(intent)
}
}
