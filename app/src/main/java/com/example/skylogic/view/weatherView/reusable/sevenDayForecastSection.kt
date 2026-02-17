package com.example.skylogic.view.weatherView.reusable

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skylogic.models.ForecastItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SevenDayForecastSection(
    daily: Map<String, List<ForecastItem>>
) {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            "5-DAY FORECAST",
            color = Color.Gray,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

        daily.entries.take(5).forEach { entry ->

            val dayItems = entry.value
            ExpandableDailyCard(entry.key, dayItems)
        }
    }
}