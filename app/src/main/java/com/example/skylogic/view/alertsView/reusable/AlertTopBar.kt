package com.example.skylogic.view.alertsView.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skylogic.R

//Top Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AlertTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlertColors.BgDeep)
            .padding(horizontal = 20.dp)
            .padding(top = 52.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.ALERTS),
            fontSize = 11.sp,
            letterSpacing = 4.sp,
            color = AlertColors.AccentCyan,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.Weather_Triggers),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = AlertColors.TextPrimary
        )
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = AlertColors.DividerColor, thickness = 1.dp)
    }
}