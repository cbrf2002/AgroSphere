package com.fsvdevs.agrosphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.ActuatorData
import com.fsvdevs.agrosphere.repository.ActuatorDataRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActuatorDataViewModel(private val repository: ActuatorDataRepository) : ViewModel() {
    private val tag = "ActuatorDataViewModel"

    val actuatorData: StateFlow<ActuatorData?> = repository.actuatorData

    init {
        loadActuatorData()
        repository.startListening()
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListening()
    }

    private fun loadActuatorData() {
        viewModelScope.launch {
            Log.d(tag, "Loading actuator data")
            repository.fetchActuatorData()
        }
    }

    fun updateActuatorData(newData: ActuatorData) {
        viewModelScope.launch {
            repository.updateActuatorData(newData)
        }
    }
}