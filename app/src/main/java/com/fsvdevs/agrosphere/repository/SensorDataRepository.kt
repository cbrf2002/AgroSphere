package com.fsvdevs.agrosphere.repository

import android.util.Log
import com.fsvdevs.agrosphere.models.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SensorDataRepository(private val database: DatabaseReference) {
    private val tag = "SensorDataRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val sensorHistoryCollection = firestore.collection("sensorHistory")

    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private var firestoreSaveInterval: Long = 600000L // Default to 10 minutes
    private var lastFirestoreSaveTime: Long = 0
    private var latestSensorData: SensorData? = null // Store the latest sensor data

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData

    private val sensorDataListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val data = snapshot.getValue(SensorData::class.java)

                // Only update if the data is complete (i.e., all sensor values are present)
                data?.let {
                    if (isCompleteSensorData(it)) {
                        _sensorData.value = it
                        latestSensorData = it

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastFirestoreSaveTime >= firestoreSaveInterval) {
                            CoroutineScope(Dispatchers.IO).launch {
                                saveSensorDataToFirestore(it)
                            }
                        }
                    }
                }
            } else {
                Log.w(tag, "No sensor data found")
            }
        }

        private fun isCompleteSensorData(sensorData: SensorData): Boolean {
            // Ensure that all sensor fields have meaningful values
            return sensorData.humidity != 0.0 &&
                    sensorData.lightLevel != 0.0 &&
                    sensorData.pH != 0.0 &&
                    sensorData.temperature != 0.0 &&
                    sensorData.waterLevel != 0.0 &&
                    sensorData.waterTemp != 0.0
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(tag, "Failed to fetch sensor data", error.toException())
        }
    }

    init {
        fetchRemoteConfig()
        startListeningForSensorData()
    }

    private fun fetchRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firestoreSaveInterval = remoteConfig.getLong("firestore_save_interval")
                Log.d(tag, "Remote Config fetched: Firestore Save Interval = $firestoreSaveInterval ms")
            }
        }
    }

    private fun startListeningForSensorData() {
        database.child("sensorData").addValueEventListener(sensorDataListener)
    }

    // Save only the latest sensor data to Firestore every SaveInterval, but check if timestamp changed
    private suspend fun saveSensorDataToFirestore(sensorData: SensorData) {
        val currentTime = System.currentTimeMillis()

        // Avoid uploading if the same timestamp exists in Firestore
        if (currentTime - lastFirestoreSaveTime >= firestoreSaveInterval) {
            try {
                // Check for duplicates based on timestamp
                val existingDoc = sensorHistoryCollection
                    .whereEqualTo("timestamp", sensorData.timestamp)
                    .limit(1)
                    .get()
                    .await()

                if (existingDoc.isEmpty) {
                    // Save new sensor data
                    val docRef = sensorHistoryCollection.document()
                    firestore.runTransaction { transaction ->
                        transaction.set(docRef, sensorData)
                    }.await()

                    lastFirestoreSaveTime = currentTime // Update the last save time
                    Log.d(tag, "Saved sensor data to Firestore: $sensorData")
                } else {
                    Log.d(tag, "Duplicate sensor data, skipping Firestore save.")
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to save sensor data to Firestore", e)
            }
        } else {
            Log.d(tag, "Skipping Firestore save, interval not reached")
        }
    }

    fun stopListeningForSensorData() {
        database.child("sensorData").removeEventListener(sensorDataListener)
    }

    suspend fun getSensorHistoryByTimePeriod(periodInMillis: Long): List<SensorData> {
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - periodInMillis
        return getDataByTimeRange(startTime, currentTime)
    }

    private suspend fun getDataByTimeRange(startTime: Long, endTime: Long): List<SensorData> {
        return withContext(Dispatchers.IO) {
            try {
                val query = firestore.collection("sensorHistory")
                    .whereGreaterThanOrEqualTo("timestamp", startTime)
                    .whereLessThanOrEqualTo("timestamp", endTime)
                    .orderBy("timestamp")
                    .limit(100)

                val results = mutableListOf<SensorData>()
                var lastVisibleDocument: com.google.firebase.firestore.DocumentSnapshot? = null

                // Paginated fetch in chunks of 100
                do {
                    val snapshot = if (lastVisibleDocument == null) {
                        query.get().await()
                    } else {
                        query.startAfter(lastVisibleDocument).get().await()
                    }

                    results.addAll(snapshot.documents.mapNotNull { it.toObject(SensorData::class.java) })
                    lastVisibleDocument = snapshot.documents.lastOrNull()
                } while (snapshot.size() >= 100)

                results
            } catch (e: Exception) {
                Log.e(tag, "Error fetching sensor data for time range", e)
                emptyList()
            }
        }
    }
}