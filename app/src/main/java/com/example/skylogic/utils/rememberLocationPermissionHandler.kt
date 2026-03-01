package com.example.skylogic.utils


import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun rememberLocationPermissionHandler(
    locationHelper: LocationHelper,
    onLocationReady: (lat: Double, lon: Double) -> Unit,
    onPermissionDenied: () -> Unit,
    apiUnit: String,
    apiLang: String
): ManagedActivityResultLauncher<String, Boolean> {

    val currentApiUnit by rememberUpdatedState(apiUnit)
    val currentApiLang by rememberUpdatedState(apiLang)
    val currentOnLocationReady by rememberUpdatedState(onLocationReady)

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationHelper.getCurrentLocation { location ->
                if (location != null) {
                    currentOnLocationReady(location.latitude, location.longitude)
                } else {
                    // will be handled by caller via setError
                    currentOnLocationReady(Double.NaN, Double.NaN)
                }
            }
        } else {
            onPermissionDenied()
        }
    }
}