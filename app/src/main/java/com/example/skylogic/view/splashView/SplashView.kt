package com.example.skylogic.view.splashView

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SkyLogicSplashScreen(
    onSplashComplete: () -> Unit
) {
    // Block back press during splash
    BackHandler(enabled = true) {}

    // Fade in animation
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "fadeIn"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(4_000L)
        onSplashComplete()
    }

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050D1A),
                        Color(0xFF0A1628),
                        Color(0xFF1E3A5F)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha)
        ) {
            Text(
                text = "⛅",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // App name
            Text(
                text = "SKY",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "LOGIC",
                fontSize = 52.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF00D4AA),
                letterSpacing = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Weather · Forecast · Atmosphere",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF56CCF2).copy(alpha = 0.8f),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}