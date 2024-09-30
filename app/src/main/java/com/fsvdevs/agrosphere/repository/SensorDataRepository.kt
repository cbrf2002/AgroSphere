package com.fsvdevs.agrosphere.repository

import android.util.Log
import com.fsvdevs.agrosphere.models.SensorData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SensorDataRepository {
    private val db = FirebaseFirestore.getInstance()
    private val sensorCollection = db.collection("sensorData")

    suspend fun getLatestSensorData(): SensorData? {
        return try {
            val querySnapshot = sensorCollection
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get().await()

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents.firstOrNull()?.toObject(SensorData::class.java)
            } else {
                Log.e("SensorDataRepository", "No sensor data found")
                null
            }
        } catch (e: Exception) {
            Log.e("SensorDataRepository", "Error fetching sensor data: ${e.message}")
            null
        }
    }
}