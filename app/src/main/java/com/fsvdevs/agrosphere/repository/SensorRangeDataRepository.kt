package com.fsvdevs.agrosphere.repository

import android.util.Log
import com.fsvdevs.agrosphere.models.SensorRangeData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class SensorRangeDataRepository(private val database: DatabaseReference) {
    private val tag = "SensorRangeDataRepository"

    private val _sensorRangeData = MutableStateFlow<SensorRangeData?>(null)
    val sensorRangeData: StateFlow<SensorRangeData?> = _sensorRangeData

    private var sensorRangeDataListener: ValueEventListener? = null
    private var isListenerAttached = false // Flag to prevent multiple listeners
    fun startListening() {
        if (isListenerAttached) {
            Log.d(tag, "Listener already attached to 'sensorRanges'.")
            return // Avoid attaching multiple listeners
        }
        if (sensorRangeDataListener == null) {
            sensorRangeDataListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(tag, "Sensor range listener received update.") // Log listener activity
                    val data = snapshot.getValue(SensorRangeData::class.java)
                    _sensorRangeData.value = data
                    Log.d(tag, "Sensor range data updated from listener: $data")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "Failed to listen for sensor range data", error.toException())
                }
            }
        }
        database.child("sensorRanges").addValueEventListener(sensorRangeDataListener!!)
        isListenerAttached = true
        Log.d(tag, "Started listening to 'sensorRanges'")
    }
    fun stopListening() {
        sensorRangeDataListener?.let {
            database.child("sensorRanges").removeEventListener(it)
            isListenerAttached = false // Reset flag
            Log.d(tag, "Stopped listening to 'sensorRanges'")
        }
    }

    suspend fun fetchSensorRangeData() {
        try {
            val sensorRangeSnapshot = database.child("sensorRanges").get().await()
            if (sensorRangeSnapshot.exists()) {
                val data = sensorRangeSnapshot.getValue(SensorRangeData::class.java)
                _sensorRangeData.value = data
                Log.d(tag, "Fetched sensor range data: $data")
            } else {
                Log.w(tag, "No sensor range data found at 'sensorRanges'")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch sensor range data", e)
        }
    }

    suspend fun updateSensorRangeData(newData: SensorRangeData) {
        try {
            database.child("sensorRanges").setValue(newData).await()
            Log.d(tag, "Updated sensor range data: $newData")
        } catch (e: Exception) {
            Log.e(tag, "Failed to update sensor range data", e)
        }
    }
}