package com.example.skylogic.view.alertsView.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
 fun ConditionChip(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg     = if (selected) accentColor.copy(alpha = 0.15f) else AlertColors.ChipBg
    val border = if (selected) accentColor.copy(alpha = 0.6f) else AlertColors.ChipBorder
    val textClr = if (selected) accentColor else AlertColors.TextSub

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .then(Modifier.padding(0.dp))
    ) {

        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, null, tint = textClr, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(4.dp))
                Text(label, color = textClr, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
}