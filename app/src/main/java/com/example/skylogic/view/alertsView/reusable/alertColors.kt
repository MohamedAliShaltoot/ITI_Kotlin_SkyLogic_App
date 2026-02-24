package com.example.skylogic.view.alertsView.reusable

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

class AlertColors {
    companion object{
        //Palette 
         val BgDeep       = Color(0xFF080E1A)
         val BgCard       = Color(0xFF0F1929)
         val BgSheet      = Color(0xFF111D2E)
         val AccentCyan   = Color(0xFF3CE8D4)
         val AccentBlue   = Color(0xFF4F8EF7)
         val AccentAmber  = Color(0xFFFFBB57)
         val AccentRed    = Color(0xFFFF5F5F)
         val TextPrimary  = Color(0xFFF0F6FF)
         val TextSub      = Color(0xFF7A8FAD)
         val DividerColor = Color(0xFF1E2E45)
         val ChipBg       = Color(0xFF162032)
         val ChipBorder   = Color(0xFF1E3250)

         val ScreenBg = Brush.linearGradient(
            colors = listOf(BgDeep, Color(0xFF091422)),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }
}