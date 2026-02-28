package com.example.skylogic.view.weatherView.reusable

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skylogic.R
import com.example.skylogic.models.ForecastItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SevenDayForecastSection(
    daily: Map<String, List<ForecastItem>>,
    tempUnit: String = "Celsius",
    windUnit: String = "meter/sec"
) {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            stringResource(R.string.FiveDAYFORECAST),
            modifier = Modifier.padding(start=10.dp),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

        daily.entries.take(5).forEach { entry ->

            val dayItems = entry.value
            ExpandableDailyCard(entry.key, dayItems, tempUnit  = tempUnit, windUnit  = windUnit  )
        }
    }
}