package com.fsvdevs.agrosphere.repository

import android.util.Log
import com.fsvdevs.agrosphere.models.ActuatorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class ActuatorDataRepository(private val database: DatabaseReference) {
    private val tag = "ActuatorDataRepository"

    private val _actuatorData = MutableStateFlow<ActuatorData?>(null)
    val actuatorData: StateFlow<ActuatorData?> = _actuatorData

    private var actuatorDataListener: ValueEventListener? = null
    private var isListenerAttached = false // Flag to prevent multiple listeners
    fun startListening() {
        if (isListenerAttached) {
            Log.d(tag, "Listener already attached to 'actuatorStates'.")
            return // Avoid attaching multiple listeners
        }
        if (actuatorDataListener == null) {
            actuatorDataListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(tag, "Actuator listener received update.") // Log listener activity
                    val data = snapshot.getValue(ActuatorData::class.java)
                    _actuatorData.value = data
                    Log.d(tag, "Actuator data updated from listener: $data")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "Failed to listen for actuator data", error.toException())
                }
            }
        }
        database.child("actuatorStates").addValueEventListener(actuatorDataListener!!)
        isListenerAttached = true
        Log.d(tag, "Started listening to 'actuatorStates'")
    }

    // Ensure public
    fun stopListening() {
        actuatorDataListener?.let {
            database.child("actuatorStates").removeEventListener(it)
            isListenerAttached = false // Reset flag
            Log.d(tag, "Stopped listening to 'actuatorStates'")
        }
    }

    suspend fun fetchActuatorData() {
        try {
            val actuatorSnapshot = database.child("actuatorStates").get().await()
            if (actuatorSnapshot.exists()) {
                val data = actuatorSnapshot.getValue(ActuatorData::class.java)
                _actuatorData.value = data
                Log.d(tag, "Fetched actuator data: $data")
            } else {
                Log.w(tag, "No actuator data found at 'actuatorStates'")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch actuator data", e)
        }
    }

    suspend fun updateActuatorData(newData: ActuatorData) {
        try {
            database.child("actuatorStates").setValue(newData).await()
            // Log update after successful await
            Log.d(tag, "Successfully updated actuator data in Firebase: $newData")
        } catch (e: Exception) {
            Log.e(tag, "Failed to update actuator data", e)
        }
    }
}