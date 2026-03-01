package com.example.skylogic.view.alertsView

import com.example.skylogic.data.local.alert.AlertEntity


sealed class AlertUiState {
    object Loading : AlertUiState()
    object Empty : AlertUiState()
    data class Success(val alerts: List<AlertEntity>) : AlertUiState()
    data class Error(val message: String) : AlertUiState()
}
sealed class AlertEvent {
    object RequestNotificationPermission : AlertEvent()
    object PermissionAlreadyGranted : AlertEvent()
}