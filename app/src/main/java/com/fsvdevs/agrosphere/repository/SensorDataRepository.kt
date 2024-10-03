package com.fsvdevs.agrosphere.repository


import android.util.Log
import com.fsvdevs.agrosphere.models.SensorData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class SensorDataRepository(private val database: DatabaseReference) {
    private val tag = "SensorDataRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val sensorHistoryCollection = firestore.collection("sensorHistory")

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData

    suspend fun fetchSensorData() {
        try {
            val sensorDataSnapshot = database.child("sensorData").get().await()
            if (sensorDataSnapshot.exists()) {
                val data = sensorDataSnapshot.getValue(SensorData::class.java)
                _sensorData.value = data
                Log.d(tag, "Fetched sensor data: $data")

                data?.let {
                    saveSensorDataToFirestore(it)
                }
            } else {
                Log.w(tag, "No sensor data found at 'sensorData'")
                Log.d(tag, "sensorDataSnapshot: ${sensorDataSnapshot.value}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch sensor data", e)
        }
    }

    private suspend fun saveSensorDataToFirestore(sensorData: SensorData) {
        try {
            sensorHistoryCollection.add(sensorData).await()
            Log.d(tag, "Saved sensor data to Firestore: $sensorData")
        } catch (e: Exception) {
            Log.e(tag, "Failed to save sensor data to Firestore", e)
        }
    }
}