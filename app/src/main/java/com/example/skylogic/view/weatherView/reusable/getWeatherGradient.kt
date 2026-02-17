package com.example.skylogic.view.weatherView.reusable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getWeatherGradient(condition: String?): List<Color> {

    return when (condition?.lowercase()) {

        "clear sky" -> listOf(
//            Color(0xFF4DA3FF),
//            Color(0xFF1E88E5),
//            Color(0xFF1565C0)
            Color(0xFF0B1C2D),
            Color(0xFF0E2236),
            Color(0xFF0A1A2A)
        )

        "few clouds", "scattered clouds", "broken clouds" -> listOf(
            Color(0xFF78909C),
            Color(0xFF546E7A),
            Color(0xFF37474F)
        )

        "overcast clouds" -> listOf(
            Color(0xFF616161),
            Color(0xFF424242),
            Color(0xFF303030)
        )

        "light rain", "moderate rain", "heavy intensity rain" -> listOf(
            Color(0xFF263238),
            Color(0xFF37474F),
            Color(0xFF102027)
        )

        "thunderstorm" -> listOf(
            Color(0xFF2C2C54),
            Color(0xFF1B1464),
            Color(0xFF0F0F3E)
        )

        "snow" -> listOf(
            Color(0xFFE3F2FD),
            Color(0xFFBBDEFB),
            Color(0xFF90CAF9)
        )

        "mist", "haze", "fog" -> listOf(
            Color(0xFFCFD8DC),
            Color(0xFFB0BEC5),
            Color(0xFF90A4AE)
        )

        else -> listOf(
            Color(0xFF0B1C2D),
            Color(0xFF0E2236),
            Color(0xFF0A1A2A)
        )
    }
}