package com.example.skylogic.view.alertsView


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skylogic.view.alertsView.reusable.AddAlertBottomSheet
import com.example.skylogic.view.alertsView.reusable.AlertCard
import com.example.skylogic.view.alertsView.reusable.AlertColors
import com.example.skylogic.view.alertsView.reusable.AlertTopBar
import com.example.skylogic.view.alertsView.reusable.ConditionMeta
import com.example.skylogic.view.alertsView.reusable.EmptyState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
@Composable
fun AlertScreen(viewModel: AlertViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()
    val alertEvent by viewModel.alertEvent.collectAsState(initial = null)
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val hasNotificationPermission = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission.value = isGranted
        if (isGranted) {
            showSheet = true
        }
    }

    // Collect one-time events from ViewModel
    LaunchedEffect(Unit) {
        viewModel.alertEvent.collect { event ->
            when (event) {
                is AlertEvent.PermissionAlreadyGranted -> {
                    showSheet = true
                }
                is AlertEvent.RequestNotificationPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val result = snackbarHostState.showSnackbar(
                            message = "Notification permission is required to receive weather alerts.",
                            actionLabel = "Grant",
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        showSheet = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AlertColors.ScreenBg)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { AlertTopBar() },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.onAddAlertClicked(hasNotificationPermission.value)
                    },
                    containerColor = AlertColors.AccentBlue,
                    contentColor = AlertColors.BgDeep,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(12.dp),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        Icons.Default.AddAlert,
                        tint = AlertColors.TextPrimary,
                        contentDescription = "Add Alert",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        ) { padding ->

            when (val state = uiState) {

                is AlertUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AlertColors.AccentCyan,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                is AlertUiState.Empty -> {
                    EmptyState(modifier = Modifier.padding(padding))
                }

                is AlertUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = AlertColors.AccentCyan,
                            fontSize = 14.sp
                        )
                    }
                }

                is AlertUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        itemsIndexed(
                            items = state.alerts,
                            key = { _, alert -> alert.id }
                        ) { index, alert ->
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







