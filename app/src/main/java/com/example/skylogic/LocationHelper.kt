package com.example.skylogic

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import android.location.Location


class LocationHelper(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onLocationResult: (android.location.Location?) -> Unit) {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                onLocationResult(location)
            }
    }
}

