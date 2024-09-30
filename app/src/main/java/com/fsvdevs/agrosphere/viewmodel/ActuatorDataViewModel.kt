package com.fsvdevs.agrosphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsvdevs.agrosphere.models.ActuatorData
import com.fsvdevs.agrosphere.repository.ActuatorDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActuatorDataViewModel : ViewModel() {
    private val repository = ActuatorDataRepository()
    private val _actuatorData = MutableStateFlow<ActuatorData?>(null)
    val actuatorData = _actuatorData.asStateFlow()

    init {
        fetchLatestActuatorData()
    }

    // Fetch the latest actuator data
    private fun fetchLatestActuatorData() {
        viewModelScope.launch {
            _actuatorData.value = repository.getLatestActuatorData()
        }
    }

    suspend fun createNewActuatorDataEntry(actuatorData: ActuatorData) {
        repository.createNewActuatorDataEntry(actuatorData)
        // After creation, fetch the updated data
        fetchLatestActuatorData()
    }
}