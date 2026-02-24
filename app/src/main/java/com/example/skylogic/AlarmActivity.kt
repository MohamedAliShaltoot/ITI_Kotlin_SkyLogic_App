package com.example.skylogic

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.core.os.postDelayed
import androidx.lifecycle.lifecycleScope
import com.example.skylogic.view.weatherView.weatherViewModel.WeatherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

class AlarmActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private val viewModel: WeatherViewModel by viewModels()


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
            AlarmScreen(
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
fun AlarmScreen(onStop: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D47A1)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Weather Alert!",
                color = Color.White,
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                shape = CircleShape
            ) {
                Text("STOP", color = Color.White)
            }
        }
    }
}
