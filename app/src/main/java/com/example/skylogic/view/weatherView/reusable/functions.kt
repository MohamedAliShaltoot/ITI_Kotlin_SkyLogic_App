package com.example.skylogic.view.weatherView.reusable

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

fun formatFullDateTime(timestamp: Long, timezone: Int): String {
    val date = Date((timestamp + timezone) * 1000)
    val sdf = SimpleDateFormat("EEE, dd MMM • hh:mm a", Locale.getDefault())
    return sdf.format(date)
}

fun formatUnixTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}
@RequiresApi(Build.VERSION_CODES.O)
fun formatDay(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateString, formatter)
    return date.dayOfWeek.getDisplayName(
        TextStyle.SHORT,
        Locale.getDefault()
    )
}
fun convertWindSpeed(speed: Double, unit: String): Double {
    return when (unit) {
        "miles/hour" -> speed * 2.237
        else -> speed // meter/sec
    }
}