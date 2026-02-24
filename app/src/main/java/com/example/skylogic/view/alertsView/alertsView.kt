package com.example.skylogic.view.alertsView


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skylogic.view.alertsView.reusable.AddAlertBottomSheet
import com.example.skylogic.view.alertsView.reusable.AlertCard
import com.example.skylogic.view.alertsView.reusable.AlertColors
import com.example.skylogic.view.alertsView.reusable.AlertTopBar
import com.example.skylogic.view.alertsView.reusable.ConditionMeta
import com.example.skylogic.view.alertsView.reusable.EmptyState
import java.text.SimpleDateFormat
import java.util.*

// AlertScreen
@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
@Composable
fun AlertScreen(viewModel: AlertViewModel = viewModel()) {
  //  val alerts by viewModel.alerts.collectAsState()
    val alerts by viewModel.getAllAlerts().collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AlertColors.ScreenBg)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { AlertTopBar() },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showSheet = true },
                    containerColor = AlertColors.AccentCyan,
                    contentColor = AlertColors.BgDeep,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(12.dp),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Alert", modifier = Modifier.size(26.dp))
                }
            }
        ) { padding ->
            if (alerts.isEmpty()) {
                EmptyState(modifier = Modifier.padding(padding))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    itemsIndexed(alerts) { index, alert ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        ) {
                            AlertCard(
                                alert = alert,
                                onDelete = { viewModel.deleteAlert(alert) }
                            )
                        }
                    }
                }
            }
        }

        if (showSheet) {
            AddAlertBottomSheet(
                onDismiss = { showSheet = false },
                onSave = { start, end, type, condition, threshold ->
                    viewModel.addAlert(start, end, type, condition, threshold)
                    showSheet = false
                }
            )
        }
    }
}

fun formatFullDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
val conditionMap = mapOf(
    "rain"      to ConditionMeta("Rain",       Icons.Outlined.WaterDrop, AlertColors.AccentBlue),
    "snow"      to ConditionMeta("Snow",       Icons.Outlined.AcUnit,    Color(0xFFB0D4FF)),
    "temp_high" to ConditionMeta("High Temp",  Icons.Outlined.Thermostat,AlertColors.AccentAmber),
    "temp_low"  to ConditionMeta("Low Temp",   Icons.Outlined.Thermostat,Color(0xFF89CFFF)),
    "wind"      to ConditionMeta("Wind", Icons.Outlined.Air, AlertColors.AccentCyan)
)







