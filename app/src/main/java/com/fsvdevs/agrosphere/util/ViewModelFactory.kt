package com.fsvdevs.agrosphere.util

import androidx.activity.ComponentActivity // Use androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.fsvdevs.agrosphere.repository.ActuatorDataRepository
import com.fsvdevs.agrosphere.repository.SensorDataRepository
import com.fsvdevs.agrosphere.repository.SensorRangeDataRepository
import com.fsvdevs.agrosphere.viewmodel.ActuatorDataViewModel
import com.fsvdevs.agrosphere.viewmodel.SensorDataViewModel
import com.fsvdevs.agrosphere.viewmodel.SensorRangeDataViewModel

object ViewModelFactory {
    fun provideSensorDataViewModel(activity: ComponentActivity): SensorDataViewModel {
        return ViewModelProvider(activity, SensorDataViewModel.Factory(SensorDataRepository(FirebaseHelper.database, activity)))[SensorDataViewModel::class.java]
    }

    fun provideActuatorDataViewModel(activity: ComponentActivity): ActuatorDataViewModel {
        return ViewModelProvider(activity, ActuatorDataViewModel.Factory(ActuatorDataRepository(FirebaseHelper.database)))[ActuatorDataViewModel::class.java]
    }

    fun provideSensorRangeDataViewModel(activity: ComponentActivity): SensorRangeDataViewModel {
        return ViewModelProvider(activity, SensorRangeDataViewModel.Factory(SensorRangeDataRepository(FirebaseHelper.database)))[SensorRangeDataViewModel::class.java]
    }
}