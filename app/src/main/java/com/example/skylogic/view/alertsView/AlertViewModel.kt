package com.example.skylogic.view.alertsView

import android.Manifest
import android.app.Application
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.skylogic.data.local.alert.AlertEntity
import com.example.skylogic.data.local.LocalDataSource
import com.example.skylogic.data.remote.RemoteDataSource
import com.example.skylogic.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlertViewModel(application: Application) : AndroidViewModel(application) {

    private val remote = RemoteDataSource()
    private val local = LocalDataSource(application)
    private val appRepository = AppRepository(local, remote)

    private val _uiState = MutableStateFlow<AlertUiState>(AlertUiState.Loading)
    val uiState: StateFlow<AlertUiState> = _uiState

    init {
        viewModelScope.launch {
            appRepository.getAllAlerts().collect { list ->
                _uiState.value = when {
                    list.isEmpty() -> AlertUiState.Empty
                    else           -> AlertUiState.Success(list)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun addAlert(
        start: Long,
        end: Long,
        type: String,
        condition: String,
        threshold: Double?
    ) {
        viewModelScope.launch {
            try {
                val id = appRepository.insertAlert(
                    AlertEntity(
                        startTime = start,
                        endTime   = end,
                        type      = type,
                        condition = condition,
                        threshold = threshold
                    )
                )
                val alertId = id.toInt()
                val inputData = workDataOf("ALERT_ID" to alertId)
                val now = System.currentTimeMillis()

                //OneTimeWorkRequest: fires exactly at startTime
                val initialDelay = (start - now).coerceAtLeast(0L)

                val oneTimeRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                    .setInputData(inputData)
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .build()

                WorkManager.getInstance(getApplication())
                    .enqueueUniqueWork(
                        "weather_alert_once_$alertId",
                        ExistingWorkPolicy.REPLACE,
                        oneTimeRequest
                    )

                // PeriodicWorkRequest: keeps checking every 15 min during active window
                // Only schedule if the window is long enough to benefit from periodic checks
                val windowDuration = end - start
                if (windowDuration > 15 * 60 * 1000L) {
                    val periodicRequest = PeriodicWorkRequestBuilder<WeatherAlertWorker>(
                        15, TimeUnit.MINUTES
                    )
                        .setInputData(inputData)
                        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                        .build()

                    WorkManager.getInstance(getApplication())
                        .enqueueUniquePeriodicWork(
                            "weather_alert_periodic_$alertId",
                            ExistingPeriodicWorkPolicy.REPLACE,
                            periodicRequest
                        )
                }
            } catch (e: Exception) {
                _uiState.value = AlertUiState.Error(e.message ?: "Failed to add alert")
            }
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            try {
                appRepository.deleteAlert(alert)
                val wm = WorkManager.getInstance(getApplication())

                // Cancel both workers
                wm.cancelUniqueWork("weather_alert_once_${alert.id}")
                wm.cancelUniqueWork("weather_alert_periodic_${alert.id}")
            } catch (e: Exception) {
                _uiState.value = AlertUiState.Error(e.message ?: "Failed to delete alert")
            }
        }
    }

}

