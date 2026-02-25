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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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

@Composable
fun HeaderSection(weather: CurrentWeatherResponse) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(Modifier.width(6.dp))

            Text(
                "${weather.name}, ${weather.sys.country}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = formatFullDateTime(weather.dt, weather.timezone),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = "https://openweathermap.org/img/wn/${weather.weather.first().icon}@2x.png",
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                weather.weather.first().description.replaceFirstChar { it.uppercase() },
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Divider(color = Color.White.copy(alpha = 0.2f))

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            MiniHeaderInfo(stringResource(R.string.Visibility), "${weather.visibility / 1000} ${
                stringResource(
                    R.string.km
                )
            }")
            MiniHeaderInfo(stringResource(R.string.Clouds), weather.weather.first().main)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    stringResource(R.string.Wind),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {

                    RotatingWindArrow(weather.wind.deg)

                    Spacer(Modifier.width(4.dp))

                    Text(
                        "${weather.wind.deg}°",
                        color = Color(0xFF4DA3FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}