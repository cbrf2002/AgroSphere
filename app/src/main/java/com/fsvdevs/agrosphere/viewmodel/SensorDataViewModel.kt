package com.fsvdevs.agrosphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.repository.SensorDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorDataViewModel(private val repository: SensorDataRepository) : ViewModel() {
    val sensorData: StateFlow<SensorData?> = repository.sensorData

    private val _selectedTimeRange = MutableStateFlow("Hour")
    val selectedTimeRange: StateFlow<String> = _selectedTimeRange

    private val _sensorHistory = MutableStateFlow<List<SensorData>>(emptyList())
    val sensorHistory: StateFlow<List<SensorData>> = _sensorHistory

    // Caching sensor history to avoid multiple Firestore reads for the same time range
    private val cachedSensorHistory = mutableMapOf<String, List<SensorData>>()

    // Debouncing to avoid redundant fetches
    private var lastFetchTime: Long = 0
    private val fetchInterval = 600000L // 10 minutes

    fun fetchSensorHistoryByRange(timeRange: String) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            if (cachedSensorHistory.containsKey(timeRange) && currentTime - lastFetchTime < fetchInterval) {
                _sensorHistory.value = cachedSensorHistory[timeRange] ?: emptyList()
            } else {
                val data = repository.getSensorHistoryByRange(timeRange)
                cachedSensorHistory[timeRange] = data
                _sensorHistory.value = data
                lastFetchTime = currentTime
            }
        }
    }

    init {
        fetchSensorHistoryByRange("Hour")
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListeningForSensorData()
    }
}