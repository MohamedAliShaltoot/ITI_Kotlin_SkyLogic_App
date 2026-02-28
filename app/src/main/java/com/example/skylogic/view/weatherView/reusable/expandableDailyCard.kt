package com.example.skylogic.view.weatherView.reusable

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skylogic.R
import coil.compose.AsyncImage
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.utils.GlassCard
import com.example.skylogic.utils.UnitSymbol

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpandableDailyCard(
    date: String,
    dayItems: List<ForecastItem>,
    windUnit: String = "meter/sec",
    tempUnit: String = "Celsius"
) {

    var expanded by remember { mutableStateOf(false) }
    val tSymbol = UnitSymbol.temp(tempUnit)
    val wSymbol = UnitSymbol.wind(windUnit)
    val maxTemp = dayItems.maxOf { it.main.temp }
    val minTemp = dayItems.minOf { it.main.temp }
    val avgHumidity = dayItems.map { it.main.humidity }.average()
    val avgWind = dayItems.map { it.wind.speed }.average()
    val maxRain = dayItems.maxOf { it.pop } * 100
    val icon = dayItems.first().weather.first().icon

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    formatDay(date),
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold
                )

                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                       // "${stringResource(R.string.H)} ${maxTemp.toInt()}°  ${stringResource(R.string.L)} ${minTemp.toInt()}°",
                        "${stringResource(R.string.H)} ${maxTemp.toInt()}$tSymbol  ${stringResource(R.string.L)} ${minTemp.toInt()}$tSymbol",
                        color = Color.White
                    )
                    Text(
                        "${stringResource(R.string.Rain)} ${maxRain.toInt()}%",
                        color = Color.Cyan,
                        fontSize = 12.sp
                    )
                }
            }

            if (expanded) {

                Spacer(Modifier.height(12.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                WeatherInfoItem(stringResource(R.string.AvgHumidity), "${avgHumidity.toInt()}%")
                WeatherInfoItem(stringResource(R.string.AvgWind),
                    "${avgWind.toInt()} $wSymbol"
//                    "${avgWind.toInt()} ${
//                    stringResource(
//                        R.string.MeterPerSecond
//                    )
//                }"
                )
                WeatherInfoItem(stringResource(R.string.PressureRange),
                    "${dayItems.minOf { it.main.pressure }} - ${dayItems.maxOf { it.main.pressure }} ${
                        stringResource(
                            R.string.hPa
                        )
                    }"
                )
            }
        }
    }
}