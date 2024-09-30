package com.fsvdevs.agrosphere.repository

import com.fsvdevs.agrosphere.models.ActuatorData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ActuatorDataRepository {
    private val db = FirebaseFirestore.getInstance()
    private val actuatorCollection = db.collection("actuatorData")

    suspend fun getLatestActuatorData(): ActuatorData? {
        return try {
            val querySnapshot = actuatorCollection
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get().await()
            querySnapshot.documents.firstOrNull()?.toObject(ActuatorData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createNewActuatorDataEntry(actuatorData: ActuatorData) {
        // Add a new entry with the current timestamp
        actuatorCollection.add(actuatorData.copy(timestamp = com.google.firebase.Timestamp.now())).await()
    }
}