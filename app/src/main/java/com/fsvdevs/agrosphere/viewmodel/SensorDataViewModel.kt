package com.fsvdevs.agrosphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.repository.SensorDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SensorDataViewModel : ViewModel() {
    private val repository = SensorDataRepository()
    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData = _sensorData.asStateFlow()

    init {
        fetchLatestSensorData()
    }

    // Fetch the latest sensor data every 5 seconds
    private fun fetchLatestSensorData() {
        viewModelScope.launch {
            while (true) {
                try {
                    val data = repository.getLatestSensorData()
                    if (data != null && data != _sensorData.value) {
                        _sensorData.value = data
                    }
                } catch (e: Exception) {
                    // Handle errors
                }
                delay(1000)
            }
        }
    }
}