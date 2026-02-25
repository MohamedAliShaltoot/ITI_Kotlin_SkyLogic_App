package com.example.skylogic

import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AlarmActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        playAlarm()
        lifecycleScope.launch {
            val endTime = intent.getLongExtra("END_TIME", 0L)

            if (endTime > System.currentTimeMillis()) {

                val duration = endTime - System.currentTimeMillis()

                delay(duration)

                stopAlarm()
                finish()
            }
        }

        setContent {

            val title = intent.getStringExtra("TITLE") ?: "Weather Alert"
            val city = intent.getStringExtra("CITY") ?: ""
            val temp = intent.getDoubleExtra("TEMP", 0.0)
            val description = intent.getStringExtra("DESCRIPTION") ?: ""

            AlarmScreen(
                title = title,
                city = city,
                temp = temp,
                description = description,
                onStop = {
                    stopAlarm()
                    finish()
                }
            )
        }
    }

    private fun playAlarm() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alram_sound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }
}
@Composable
fun AlarmScreen(
    title: String,
    city: String,
    temp: Double,
    description: String,
    onStop: () -> Unit
) {

    val backgroundColor = when (title) {
        "rain" -> Color(0xFF1565C0)
        "snow" -> Color(0xFF546E7A)
        "wind" -> Color(0xFF37474F)
        "temp_high" -> Color(0xFFD32F2F)
        "temp_low" -> Color(0xFF0288D1)
        else -> Color(0xFF0D47A1)
    }

    val emoji = when (title) {
        "rain" -> "🌧"
        "snow" -> "❄"
        "wind" -> "🌬"
        "temp_high" -> "🌡"
        "temp_low" -> "🥶"
        else -> "⚠"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "$emoji Weather Alert",
                color = Color.White,
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📍 $city",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Condition: ${description.replaceFirstChar { it.uppercase() }}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Temp: $temp°C",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "STOP ALARM",
                    color = Color.White
                )
            }
        }
    }
}

