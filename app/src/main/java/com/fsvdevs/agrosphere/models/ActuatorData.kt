package com.fsvdevs.agrosphere.models

data class ActuatorData(
    var fan: Boolean = false,
    var mist: Boolean = false,
    var vent: Boolean = false
)