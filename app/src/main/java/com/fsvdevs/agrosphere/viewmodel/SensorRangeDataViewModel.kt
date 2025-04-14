package com.fsvdevs.agrosphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.SensorRangeData
import com.fsvdevs.agrosphere.repository.SensorRangeDataRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorRangeDataViewModel(private val repository: SensorRangeDataRepository) : ViewModel() {
    private val tag = "SensorRangeDataViewModel"

    val sensorRangeData: StateFlow<SensorRangeData?> = repository.sensorRangeData

    init {
        loadSensorRangeData()
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListening()
    }

    fun loadSensorRangeData() {
        viewModelScope.launch {
            Log.d(tag, "Loading sensor range data")
            repository.fetchSensorRangeData()
            repository.startListening()
        }
    }

    fun updateSensorRangeData(newData: SensorRangeData) {
        viewModelScope.launch {
            repository.updateSensorRangeData(newData)
        }
    }

    fun stopListeners() {
        Log.d(tag, "Explicitly stopping sensor range listeners.")
        repository.stopListening()
    }

    class Factory(private val repository: SensorRangeDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SensorRangeDataViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SensorRangeDataViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}