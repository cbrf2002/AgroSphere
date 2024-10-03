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

    private var actuatorListener: ValueEventListener? = null

    /**
     * Starts listening to changes in the 'actuatorStates' node.
     */
    fun startListening() {
        if (actuatorListener == null) {
            actuatorListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(ActuatorData::class.java)
                        _actuatorData.value = data
                        Log.d(tag, "Fetched actuator data: $data")
                    } else {
                        Log.w(tag, "No actuator data found at 'actuatorStates'")
                        Log.d(tag, "actuatorDataSnapshot: ${snapshot.value}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "Failed to listen for actuator data changes", error.toException())
                }
            }
            database.child("actuatorStates").addValueEventListener(actuatorListener!!)
            Log.d(tag, "Started listening to 'actuatorStates'")
        }
    }

    /**
     * Stops listening to changes in the 'actuatorStates' node.
     */
    fun stopListening() {
        actuatorListener?.let {
            database.child("actuatorStates").removeEventListener(it)
            actuatorListener = null
            Log.d(tag, "Stopped listening to 'actuatorStates'")
        }
    }

    /**
     * Fetches actuator data once.
     * (Optional if you prefer real-time updates only)
     */
    suspend fun fetchActuatorData() {
        try {
            val actuatorDataSnapshot = database.child("actuatorStates").get().await()
            if (actuatorDataSnapshot.exists()) {
                val data = actuatorDataSnapshot.getValue(ActuatorData::class.java)
                _actuatorData.value = data
                Log.d(tag, "Fetched actuator data: $data")
            } else {
                Log.w(tag, "No actuator data found at 'actuatorStates'")
                Log.d(tag, "actuatorDataSnapshot: ${actuatorDataSnapshot.value}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch actuator data", e)
        }
    }

    /**
     * Updates actuator data in Firebase.
     */
    suspend fun updateActuatorData(newData: ActuatorData) {
        try {
            database.child("actuatorStates").setValue(newData).await()
            Log.d(tag, "Updated actuator data: $newData")
        } catch (e: Exception) {
            Log.e(tag, "Failed to update actuator data", e)
        }
    }
}