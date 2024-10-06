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

    private var sensorRangeListener: ValueEventListener? = null

    /**
     * Starts listening to changes in the 'sensorRanges' node.
     */
    fun startListening() {
        if (sensorRangeListener == null) {
            sensorRangeListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(SensorRangeData::class.java)
                        _sensorRangeData.value = data
                        Log.d(tag, "Fetched sensor range data: $data")
                    } else {
                        Log.w(tag, "No sensor range data found at 'sensorRanges'")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "Failed to listen for sensor range data changes", error.toException())
                }
            }
            database.child("sensorRanges").addValueEventListener(sensorRangeListener!!)
            Log.d(tag, "Started listening to 'sensorRanges'")
        }
    }

    /**
     * Stops listening to changes in the 'sensorRanges' node.
     */
    fun stopListening() {
        sensorRangeListener?.let {
            database.child("sensorRanges").removeEventListener(it)
            sensorRangeListener = null
            Log.d(tag, "Stopped listening to 'sensorRanges'")
        }
    }

    /**
     * Fetches sensor range data once.
     */
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

    /**
     * Updates sensor range data in Firebase.
     */
    suspend fun updateSensorRangeData(newData: SensorRangeData) {
        try {
            database.child("sensorRanges").setValue(newData).await()
            Log.d(tag, "Updated sensor range data: $newData")
        } catch (e: Exception) {
            Log.e(tag, "Failed to update sensor range data", e)
        }
    }
}