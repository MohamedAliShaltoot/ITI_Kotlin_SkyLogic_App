package com.example.skylogic.view.weatherView.reusable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skylogic.R
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.utils.GlassCard
import com.example.skylogic.utils.UnitSymbol

@Composable
fun CurrentWeatherSection(
    weather: CurrentWeatherResponse,
    windUnit: String,
    tempUnit: String,
) {

    val windSpeed = convertWindSpeed(weather.wind.speed, windUnit)
    val tSymbol   = UnitSymbol.temp(tempUnit)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(36.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "${weather.main.temp.toInt()}$tSymbol",
                   // "${weather.main.temp.toInt()}°",
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(16.dp))

                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${weather.weather.first().icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                weather.weather.first().description.replaceFirstChar { it.uppercase() },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp
            )

            Text(
                "${stringResource(R.string.FeelsLike)} ${weather.main.feels_like.toInt()}$tSymbol",
               // "${stringResource(R.string.FeelsLike)} ${weather.main.feels_like.toInt()}°",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            Divider(color = Color.White.copy(alpha = 0.2f))

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem(stringResource(R.string.High),
                   // "${weather.main.temp_max.toInt()}°"
                    "${weather.main.temp_max.toInt()}$tSymbol"
                )
                WeatherInfoItem(stringResource(R.string.Low),
                   // "${weather.main.temp_min.toInt()}°"
                    "${weather.main.temp_min.toInt()}$tSymbol"
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem(stringResource(R.string.Humidity), "${weather.main.humidity}%")
                WeatherInfoItem(
                    stringResource(R.string.Wind),
                    "${windSpeed.toInt()} ${if (windUnit == "miles/hour") stringResource(R.string.mph) else stringResource(R.string.MeterPerSecond)}"
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem(stringResource(R.string.Pressure), "${weather.main.pressure} ${stringResource(R.string.hPa)}")
                WeatherInfoItem(stringResource(R.string.Sunrise), formatUnixTime(weather.sys.sunrise))
            }

            Spacer(Modifier.height(12.dp))

            WeatherInfoItem(stringResource(R.string.Sunset), formatUnixTime(weather.sys.sunset))
        }
    }
}