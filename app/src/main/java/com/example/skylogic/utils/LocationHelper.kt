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
                onLocationResult(location)
            }
    }
}