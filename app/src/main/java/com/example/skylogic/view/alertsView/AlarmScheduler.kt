package com.example.skylogic.view.alertsView

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.example.skylogic.AlertReceiver
import com.example.skylogic.data.local.alert.AlertEntity

class AlarmScheduler(private val context: Context) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun schedule(alert: AlertEntity) {

        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("TYPE", alert.type)
            putExtra("ID", alert.id)
            putExtra("END_TIME", alert.endTime)

        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alert.startTime,
            pendingIntent
        )
    }

    fun cancel(alert: AlertEntity) {

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
