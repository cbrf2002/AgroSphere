package com.fsvdevs.agrosphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.repository.SensorDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class SensorDataViewModel(private val repository: SensorDataRepository) : ViewModel() {
    val sensorData: StateFlow<SensorData?> = repository.sensorData
    val databaseChangeDetected = repository.databaseChangeDetected

    private val _sensorHistory = MutableStateFlow<List<SensorData>>(emptyList())
    val sensorHistory: StateFlow<List<SensorData>> = _sensorHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Track current range for refreshing
    private var currentTimeRange: String = "Hour"
    private val TAG = "SensorDataViewModel"

    init {
        // Listen for database changes detected by history listener
        viewModelScope.launch {
            repository.databaseChangeDetected.collect { hasChanged ->
                if (hasChanged) {
                    Log.d(TAG, "Database change detected, refreshing data")
                    refreshData(currentTimeRange) // Refresh history
                }
            }
        }

        fetchSensorHistoryByRange("Hour")
        loadCurrentSensorData()
    }

    fun fetchSensorHistoryByRange(timeRange: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Always update current time range regardless of force refresh
                currentTimeRange = timeRange
                Log.d(TAG, "Fetching sensor history for range: $timeRange, forceRefresh: $forceRefresh")

                // Clear existing data immediately to ensure UI reflects loading state
                if (forceRefresh) {
                    _sensorHistory.value = emptyList()
                }

                val data = repository.getSensorHistoryByTimePeriod(
                    getTimePeriodInMillis(timeRange),
                    forceRefresh
                )

                if (data.isNotEmpty()) {
                    Log.d(TAG, "Loaded ${data.size} data points for $timeRange")
                    _sensorHistory.value = data
                    _errorMessage.value = null
                } else {
                    Log.d(TAG, "No data available for $timeRange")

                    // For Hour view, try with extended time range
                    if (timeRange == "Hour") {
                        Log.d(TAG, "Trying extended range for Hour view")
                        val extendedData = repository.getSensorHistoryByTimePeriod(
                            3600000L * 12, // Look back 12 hours instead of 1
                            forceRefresh
                        )

                        if (extendedData.isNotEmpty()) {
                            Log.d(TAG, "Loaded ${extendedData.size} points with extended range")
                            // Take the most recent entries up to a reasonable number
                            val recentData = extendedData.takeLast(20)
                            _sensorHistory.value = recentData
                            _errorMessage.value = null
                        } else {
                            _sensorHistory.value = emptyList()
                            _errorMessage.value = "No data available for the selected time range"
                        }
                    } else {
                        _sensorHistory.value = emptyList()
                        _errorMessage.value = "No data available for the selected time range"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching history data: ${e.message}", e)
                _sensorHistory.value = emptyList()
                _errorMessage.value = "Error loading data: ${e.message}"
            } finally {
                _isLoading.value = false
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

    // Force refresh all cached data
    fun clearAllCaches() {
        viewModelScope.launch {
            repository.clearAllCachedData()
            refreshData(currentTimeRange)
        }
    }

    // Refresh current timerange data
    fun refreshData(timeRange: String? = null) {
        viewModelScope.launch {
            val rangeToRefresh = timeRange ?: currentTimeRange
            Log.d(TAG, "Explicitly refreshing data for range: $rangeToRefresh")
            fetchSensorHistoryByRange(rangeToRefresh, true)
        }
    }

    fun loadCurrentSensorData() {
        viewModelScope.launch {
            Log.d(TAG, "Explicitly loading current sensor data.")
            repository.fetchCurrentSensorData()
            // Start sensor data listener *after* initial fetch is complete
            repository.startListeningForSensorData()
            // Start history listener here as well, ensuring both are started post-init fetch
            repository.startListeningForHistoryChanges()
        }
    }

    fun stopListeners() {
        Log.d(TAG, "Explicitly stopping sensor data listeners.")
        repository.stopListeningForSensorData()
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