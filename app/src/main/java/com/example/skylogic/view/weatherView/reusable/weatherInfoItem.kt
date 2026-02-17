package com.example.skylogic.view.weatherView.reusable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun WeatherInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color(0xFF4DA3FF),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}