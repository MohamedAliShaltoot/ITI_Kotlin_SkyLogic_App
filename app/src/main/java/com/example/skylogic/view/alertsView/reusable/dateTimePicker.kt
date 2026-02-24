package com.example.skylogic.view.alertsView.reusable

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DateTimePicker(
    initialTimestamp: Long = System.currentTimeMillis(),
    onDateTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    var displayText by remember {
        mutableStateOf(SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault()).format(Date(initialTimestamp)))
    }

    OutlinedButton(
        onClick = {
            val calendar = Calendar.getInstance()
            android.app.DatePickerDialog(
                context,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    TimePickerDialog(context, { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        val time = calendar.timeInMillis
                        onDateTimeSelected(time)
                        displayText = SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault()).format(Date(time))
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AlertColors.ChipBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = AlertColors.ChipBg),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Icon(Icons.Outlined.CalendarMonth, null, tint =AlertColors. AccentCyan, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(8.dp))
        Text(displayText, color =AlertColors. TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}