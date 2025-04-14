package com.fsvdevs.agrosphere.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListening()
    }

    fun loadActuatorData() {
        viewModelScope.launch {
            Log.d(tag, "Loading actuator data")
            repository.fetchActuatorData()
            // Start listener *after* initial fetch is complete
            repository.startListening()
        }
    }

    fun stopListeners() {
        Log.d(tag, "Explicitly stopping actuator listeners.")
        repository.stopListening()
    }

    fun updateActuatorData(newData: ActuatorData) {
        viewModelScope.launch {
            repository.updateActuatorData(newData)
        }
    }

    class Factory(private val repository: ActuatorDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActuatorDataViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ActuatorDataViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}