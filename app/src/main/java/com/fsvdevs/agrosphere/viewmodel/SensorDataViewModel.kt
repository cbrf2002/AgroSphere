package com.fsvdevs.agrosphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.repository.SensorDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorDataViewModel(private val repository: SensorDataRepository) : ViewModel() {
    val sensorData: StateFlow<SensorData?> = repository.sensorData

    private val _sensorHistory = MutableStateFlow<List<SensorData>>(emptyList())
    val sensorHistory: StateFlow<List<SensorData>> = _sensorHistory

    private val cachedSensorHistory = mutableMapOf<String, List<SensorData>>()
    private var cacheCreationTime: Long = 0L
    private val cacheTimeout = 3600000L // 1 hour

    fun fetchSensorHistoryByRange(timeRange: String) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val isCacheExpired = currentTime - cacheCreationTime > cacheTimeout

            if (cachedSensorHistory.containsKey(timeRange) && !isCacheExpired) {
                _sensorHistory.value = cachedSensorHistory[timeRange] ?: emptyList()
            } else {
                val data = repository.getSensorHistoryByTimePeriod(getTimePeriodInMillis(timeRange))
                cachedSensorHistory[timeRange] = data
                _sensorHistory.value = data
                cacheCreationTime = currentTime
            }
        }
    }

    private fun getTimePeriodInMillis(timeRange: String): Long {
        return when (timeRange) {
            "Hour" -> 3600000L
            "Day" -> 86400000L
            "Week" -> 604800000L
            "Month" -> 2629746000L
            "Year" -> 31556952000L
            else -> 3600000L
        }
    }

    init {
        fetchSensorHistoryByRange("Hour")
    }

    // Clear the cache based on certain events or intervals
    private fun clearCache() {
        cachedSensorHistory.clear()
        cacheCreationTime = 0L
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListeningForSensorData()
    }

    class Factory(private val repository: SensorDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SensorDataViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SensorDataViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}