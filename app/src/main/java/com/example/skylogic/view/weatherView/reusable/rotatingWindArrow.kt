package com.example.skylogic.view.weatherView.reusable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RotatingWindArrow(degrees: Int) {

    val rotation by animateFloatAsState(
        targetValue = degrees.toFloat(),
        label = ""
    )

    Icon(
        imageVector = Icons.Default.Navigation,
        contentDescription = "Wind Direction",
        tint = Color(0xFF4DA3FF),
        modifier = Modifier
            .size(24.dp)
            .rotate(rotation)
    )
}