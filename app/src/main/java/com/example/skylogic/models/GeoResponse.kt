package com.example.skylogic.models

data class GeoResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)

