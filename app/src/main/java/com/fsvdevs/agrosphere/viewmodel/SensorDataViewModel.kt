package com.fsvdevs.agrosphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.repository.SensorDataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SensorDataViewModel(private val repository: SensorDataRepository) : ViewModel() {
    private val tag = "SensorDataViewModel"

    val sensorData: StateFlow<SensorData?> = repository.sensorData

    init {
        startFetchingSensorData()
    }

    private fun startFetchingSensorData() {
        viewModelScope.launch {
            while (isActive) {
                loadSensorData()
                delay(5000)
            }
        }
    }

    private fun loadSensorData() {
        viewModelScope.launch {
            Log.d(tag, "Loading sensor data")
            repository.fetchSensorData()
        }
    }
}