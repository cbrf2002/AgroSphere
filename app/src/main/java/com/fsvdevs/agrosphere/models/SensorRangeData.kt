package com.fsvdevs.agrosphere.models

data class SensorRangeData(
    val tempRangeHigh: Double = 0.0,
    val tempRangeLow: Double = 0.0,
    val humRangeHigh: Double = 0.0,
    val humRangeLow: Double = 0.0
)