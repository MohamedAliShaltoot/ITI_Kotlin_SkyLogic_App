package com.example.skylogic.view.weatherView.reusable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import coil.compose.AsyncImage
import com.example.skylogic.R
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.utils.GlassCard
import com.example.skylogic.utils.UnitSymbol


@Composable
fun ExpandableHourlyCard(item: ForecastItem,
                         windUnit: String = "meter/sec",
                         tempUnit: String = "Celsius"
                           ) {

    var expanded by remember { mutableStateOf(false) }
    val tSymbol = UnitSymbol.temp(tempUnit)
    val wSymbol = UnitSymbol.wind(windUnit)

    GlassCard(
        modifier = Modifier
            .width(200.dp)
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {

        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                item.dt_txt.substringAfter(" ").substring(0,5),
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            AsyncImage(
                model = "https://openweathermap.org/img/wn/${item.weather.first().icon}@2x.png",
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Text(
                "${item.main.temp.toInt()}$tSymbol",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "${stringResource(R.string.FeelsLike)} ${item.main.feels_like.toInt()}$tSymbol",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "${stringResource(R.string.Rain)} ${(item.pop * 100).toInt()}%",
                color = Color.Cyan,
                fontSize = 12.sp
            )

            if (expanded) {

                Spacer(Modifier.height(12.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                WeatherInfoItem(stringResource(R.string.Humidity), "${item.main.humidity}${
                    stringResource(
                        R.string.percentage
                    )
                }")
                WeatherInfoItem(stringResource(R.string.Pressure), "${item.main.pressure} ${stringResource(R.string.hPa)}")
                WeatherInfoItem(stringResource(R.string.Clouds), "${item.clouds.all}${
                    stringResource(
                        R.string.percentage
                    )
                }")
                WeatherInfoItem(stringResource(R.string.Visibility), "${item.visibility / 1000} ${stringResource(R.string.km)}")
                WeatherInfoItem(stringResource(R.string.WindSpeed),
                    "${item.wind.speed} $wSymbol"

                )
                WeatherInfoItem(stringResource(R.string.WindDirection), "${item.wind.deg}°")
                item.wind.gust?.let {
                    WeatherInfoItem(stringResource(R.string.WindGust),
                        "$it $wSymbol"
                    )
                }
            }
        }
    }
}