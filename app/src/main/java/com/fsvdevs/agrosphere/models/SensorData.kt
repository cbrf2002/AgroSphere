package com.fsvdevs.agrosphere.models

data class SensorData(
    var humidity: Double = 0.0,
    var lightLevel: Double = 0.0,
    var pH: Double = 0.0,
    var temperature: Double = 0.0,
    var timestamp: Long = 0L,
    var waterLevel: Double = 0.0,
    var waterTemp: Double = 0.0
)