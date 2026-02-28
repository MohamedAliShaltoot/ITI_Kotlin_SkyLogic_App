package com.example.skylogic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices

class LocationHelper(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationResult: (Location?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationResult(location)
                } else {
                    requestFreshLocation(onLocationResult)
                }
            }
            .addOnFailureListener {
                requestFreshLocation(onLocationResult)
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(onLocationResult: (Location?) -> Unit) {
        val request = com.google.android.gms.location.CurrentLocationRequest.Builder()
            .setPriority(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(60_000)
            .setDurationMillis(10_000)
            .build()

        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { location -> onLocationResult(location) }
            .addOnFailureListener { onLocationResult(null) }
    }
}