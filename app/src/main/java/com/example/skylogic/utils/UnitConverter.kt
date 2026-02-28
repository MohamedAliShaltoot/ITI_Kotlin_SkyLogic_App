package com.example.skylogic.utils

object UnitConverter {

    fun convertTemp(celsius: Double, unit: String): Double = when (unit) {
        "Fahrenheit" -> celsius * 9.0 / 5.0 + 32
        "Kelvin"     -> celsius + 273.15
        else         -> celsius   // "Celsius" default
    }

    fun tempSymbol(unit: String): String = when (unit) {
        "Fahrenheit" -> "°F"
        "Kelvin"     -> "K"
        else         -> "°C"
    }

    fun convertWind(meterPerSec: Double, unit: String): Double = when (unit) {
        "mile/hour" -> meterPerSec * 2.23694
        else        -> meterPerSec   // "meter/sec" default
    }

    fun windSymbol(unit: String): String = when (unit) {
        "mile/hour" -> "mph"
        else        -> "m/s"
    }
}

// utils/UnitSymbol.kt
object UnitSymbol {
    fun temp(unit: String): String = when (unit) {
        "Fahrenheit" -> "°F"
        "Kelvin"     -> "K"
        else         -> "°C"
    }

    fun wind(unit: String): String = when (unit) {
        "miles/hour" -> "mph"
        else         -> "m/s"
    }
}