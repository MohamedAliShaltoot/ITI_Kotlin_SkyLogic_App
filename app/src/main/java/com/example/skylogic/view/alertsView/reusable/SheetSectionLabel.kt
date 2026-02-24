package com.example.skylogic.view.alertsView.reusable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Reusable Sheet Chips
@Composable
fun SheetSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = AlertColors.TextSub,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp
    )
}