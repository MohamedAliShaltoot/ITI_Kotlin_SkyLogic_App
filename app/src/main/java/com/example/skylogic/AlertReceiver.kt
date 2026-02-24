package com.example.skylogic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val type = intent.getStringExtra("TYPE") ?: "notification"

        if (type == "notification") {
            showNotification(context)
        } else {
            openAlarmScreen(context, intent)
        }
    }




    private fun showNotification(context: Context) {

        val channelId = "weather_alert_channel"

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        // Create channel (required API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )

            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.app_icon) // change later
            .setContentTitle("Weather Alert")
            .setContentText("Severe weather detected!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }

    private fun openAlarmScreen(context: Context, oldIntent: Intent) {

        val newIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP

            putExtras(oldIntent.extras!!)
        }

        context.startActivity(newIntent)
    }



}
//    private fun playAlarmSound(context: Context) {
//
//        val alarmSound: Uri = RingtoneManager
//            .getDefaultUri(RingtoneManager.TYPE_ALARM)
//
//        val ringtone =
//            RingtoneManager.getRingtone(context, alarmSound)
//
//        ringtone.play()
//    }
//private fun openAlarmScreen(context: Context) {
//
//    val intent = Intent(context, AlarmActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
//                Intent.FLAG_ACTIVITY_CLEAR_TOP
//    }
//
//    context.startActivity(intent)
//}


//    override fun onReceive(context: Context, intent: Intent) {
//
//        val type = intent.getStringExtra("type") ?: "notification"
//
//        if (type == "notification") {
//            showNotification(context)
//        } else {
//            playAlarmSound(context)
//        }
//    }
//override fun onReceive(context: Context, intent: Intent) {
//
//    val type = intent.getStringExtra("TYPE") ?: "notification"
//
//    if (type == "notification") {
//        showNotification(context)
//    } else {
//        openAlarmScreen(context)
//    }
//}