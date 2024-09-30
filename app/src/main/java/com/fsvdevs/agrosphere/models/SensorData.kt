package com.fsvdevs.agrosphere.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class SensorData(
    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Timestamp = Timestamp.now(),

    @get:PropertyName("temperature") @set:PropertyName("temperature")
    var temperature: Float = 0.0f,

    @get:PropertyName("humidity") @set:PropertyName("humidity")
    var humidity: Float = 0.0f,

    @get:PropertyName("light_level") @set:PropertyName("light_level")
    var lightLevel: Int = 0,

    @get:PropertyName("water_level") @set:PropertyName("water_level")
    var waterLevel: Float = 0.0f,

    @get:PropertyName("water_temperature") @set:PropertyName("water_temperature")
    var waterTemperature: Float = 0.0f,

    @get:PropertyName("ph_level") @set:PropertyName("ph_level")
    var phLevel: Float = 0.0f
)