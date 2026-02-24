package com.example.skylogic.view.alertsView.reusable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.collections.chunked
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

private val conditionMap = mapOf(
    "rain"      to ConditionMeta("Rain",       Icons.Outlined.WaterDrop, AlertColors.AccentBlue),
    "snow"      to ConditionMeta("Snow",       Icons.Outlined.AcUnit,    Color(0xFFB0D4FF)),
    "temp_high" to ConditionMeta("High Temp",  Icons.Outlined.Thermostat,AlertColors.AccentAmber),
    "temp_low"  to ConditionMeta("Low Temp",   Icons.Outlined.Thermostat,Color(0xFF89CFFF)),
    "wind"      to ConditionMeta("Wind",       Icons.Outlined.Air,       AlertColors.AccentCyan)
)
//Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Long, Long, String, String, Double?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime   by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }
    var selectedType      by remember { mutableStateOf("notification") }
    var selectedCondition by remember { mutableStateOf("rain") }
    var thresholdText     by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AlertColors.BgSheet,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(AlertColors.DividerColor)
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "Create Alert",
                color = AlertColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Set a weather condition trigger", color = AlertColors.TextSub, fontSize = 13.sp)

            Spacer(Modifier.height(28.dp))

            // Time Section
            SheetSectionLabel("Time Window")
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Start", color =AlertColors. TextSub, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(6.dp))
                    DateTimePicker(
                        initialTimestamp = startTime,
                        onDateTimeSelected = { startTime = it }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("End", color = AlertColors.TextSub, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(Modifier.height(6.dp))
                    DateTimePicker(
                        initialTimestamp = endTime,
                        onDateTimeSelected = { endTime = it }
                    )
                }
            }

            Spacer(Modifier.height(26.dp))
            HorizontalDivider(color = AlertColors.DividerColor)
            Spacer(Modifier.height(26.dp))

            // Alert Type
            SheetSectionLabel("Alert Type")
            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TypeChip(
                    label = "Notification",
                    icon = Icons.Outlined.Notifications,
                    selected = selectedType == "notification",
                    onClick = { selectedType = "notification" }
                )
                TypeChip(
                    label = "Alarm",
                    icon = Icons.Outlined.Alarm,
                    selected = selectedType == "alarm",
                    onClick = { selectedType = "alarm" }
                )
            }

            Spacer(Modifier.height(26.dp))
            HorizontalDivider(color =AlertColors. DividerColor)
            Spacer(Modifier.height(26.dp))

            // Condition
            SheetSectionLabel("Condition")
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                conditionMap.entries.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { (key, meta) ->
                            ConditionChip(
                                label = meta.label,
                                icon = meta.icon,
                                accentColor = meta.color,
                                selected = selectedCondition == key,
                                onClick = { selectedCondition = key },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill last row if odd count
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            //Threshold
            AnimatedVisibility(
                visible = selectedCondition.contains("temp") || selectedCondition == "wind"
            ) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    val hint = when (selectedCondition) {
                        "temp_high" -> "Max temperature (°C)"
                        "temp_low"  -> "Min temperature (°C)"
                        "wind"      -> "Wind speed (m/s)"
                        else        -> "Value"
                    }
                    val isError = thresholdText.isNotEmpty() && thresholdText.toDoubleOrNull() == null

                    OutlinedTextField(
                        value = thresholdText,
                        onValueChange = { thresholdText = it },
                        label = { Text(hint, color =AlertColors. TextSub, fontSize = 13.sp) },
                        isError = isError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AlertColors.AccentCyan,
                            unfocusedBorderColor = AlertColors.ChipBorder,
                            focusedLabelColor = AlertColors.AccentCyan,
                            cursorColor = AlertColors.AccentCyan,
                            focusedTextColor =AlertColors. TextPrimary,
                            unfocusedTextColor = AlertColors.TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        supportingText = {
                            if (isError) Text("Enter a valid number", color = AlertColors.AccentRed, fontSize = 11.sp)
                        }
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // Save
            val needsThreshold = selectedCondition.contains("temp") || selectedCondition == "wind"
            val isThresholdValid = !needsThreshold || (thresholdText.isNotEmpty() && thresholdText.toDoubleOrNull() != null)
            Button(
                onClick = {
                    onSave(startTime, endTime, selectedType, selectedCondition, thresholdText.toDoubleOrNull())
                },
                enabled = isThresholdValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =AlertColors. AccentCyan,
                    contentColor = AlertColors.BgDeep
                )
            ) {
                Text("Save Alert", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

/*
// Save
val needsThreshold = selectedCondition.contains("temp") || selectedCondition == "wind"
val isThresholdValid = !needsThreshold || (thresholdText.isNotEmpty() && thresholdText.toDoubleOrNull() != null)

Button(
    onClick = {
        onSave(startTime, endTime, selectedType, selectedCondition, thresholdText.toDoubleOrNull())
    },
    enabled = isThresholdValid,   // <-- add this
    modifier = Modifier
        .fillMaxWidth()
        .height(54.dp),
    ...
)
 */