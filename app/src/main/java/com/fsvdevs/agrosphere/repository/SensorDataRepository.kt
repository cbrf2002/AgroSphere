package com.fsvdevs.agrosphere.repository

import android.content.Context
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SensorDataRepository(
    private val database: DatabaseReference,
    private val context: Context
) {
    // Constants declaration
    private val tagSensorDataRepository = "SensorDataRepository"
    private val firestore = FirebaseFirestore.getInstance()
    private val sensorHistoryCollection = firestore.collection("sensorHistory")

    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private var firestoreSaveInterval: Long = 600000L           // Default to 10 minutes
    private var lastFirestoreSaveTime: Long = 0                 // Store the last Firestore save time
    private var lastUploadedTimestamp: Long = 0                 // Store the last uploaded timestamp
    private var lastUploadedSensorData: SensorData? = null      // Store the last uploaded sensor data
    private var lastDataChangeTime: Long = 0                    // Store the last data change time

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData

    private fun sensorDataToFirestoreMap(sensorData: SensorData): Map<String, Any> {
        return mapOf(
            "carbonDioxide" to sensorData.carbonDioxide,
            "humidity" to sensorData.humidity,
            "lightLevel" to sensorData.lightLevel,
            "pH" to sensorData.pH,
            "temperature" to sensorData.temperature,
            "timestamp" to sensorData.timestamp,
            "waterTemp" to sensorData.waterTemp,
            "tds" to sensorData.tds
        )
    }

    private val sensorDataListener = object : ValueEventListener { // Listener for sensor data
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentTime = System.currentTimeMillis()

            // Check if enough time has passed since the last data change processing
            if (currentTime - lastDataChangeTime >= 10000) { // 10 seconds cooldown
                if (snapshot.exists()) {
                    val data = snapshot.getValue(SensorData::class.java)
                    data?.let {
                        if (isCompleteSensorData(it)) {
                            _sensorData.value = it

                            if (currentTime - lastFirestoreSaveTime >= firestoreSaveInterval) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    saveSensorDataToFirestore(it)
                                }
                            }
                        }
                    }
                }
                lastDataChangeTime = currentTime // Update the last data change time
            }
        }

        private fun isCompleteSensorData(sensorData: SensorData): Boolean { // Ensure that all sensor fields have meaningful values
            return sensorData.humidity >= 0.0 &&
                    sensorData.lightLevel >= 0.0 &&
                    sensorData.pH >= 0.0 &&
                    sensorData.temperature >= -20.0 &&
                    sensorData.waterTemp >= 0.0 &&
                    sensorData.tds >= 0.0
        }

        override fun onCancelled(error: DatabaseError) { // Handle errors
            Log.e(tagSensorDataRepository, "Failed to fetch sensor data", error.toException())
        }
    }

    init {
        fetchRemoteConfig()
        startListeningForSensorData()
        loadLastFirestoreTimestamps()
    }

    private fun fetchRemoteConfig() { // Fetch remote config values
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firestoreSaveInterval = remoteConfig.getLong("firestore_save_interval")
                Log.d(tagSensorDataRepository, "Remote Config fetched: Firestore Save Interval = $firestoreSaveInterval ms")
            }
        }
    }

    private fun startListeningForSensorData() {
        database.child("sensorData").addValueEventListener(sensorDataListener)
    }


    private fun loadLastFirestoreTimestamps() {
        val prefs = context.getSharedPreferences("sensor_data_prefs", Context.MODE_PRIVATE)
        lastFirestoreSaveTime = prefs.getLong("lastFirestoreSaveTime", 0L)
        lastUploadedTimestamp = prefs.getLong("lastUploadedTimestamp", 0L)
    }

    private fun saveLastFirestoreTimestamps() {
        val prefs = context.getSharedPreferences("sensor_data_prefs", Context.MODE_PRIVATE).edit()
        prefs.putLong("lastFirestoreSaveTime", lastFirestoreSaveTime)
        prefs.putLong("lastUploadedTimestamp", lastUploadedTimestamp)
        prefs.apply()
    }

    // Save only the latest sensor data to Firestore every SaveInterval, but check if timestamp changed
    private suspend fun saveSensorDataToFirestore(sensorData: SensorData) {
        delay(10000) // Delay for 10 seconds

        val currentTime = System.currentTimeMillis()

        if (sensorData.timestamp - lastUploadedTimestamp >= 600_000) {
            if (lastUploadedSensorData == null || lastUploadedSensorData != sensorData) {
                try {
                    if (currentTime - lastFirestoreSaveTime >= firestoreSaveInterval) {
                        val existingDoc = sensorHistoryCollection
                            .whereEqualTo("timestamp", sensorData.timestamp)
                            .limit(1)
                            .get()
                            .await()

                        if (existingDoc.isEmpty) {
                            val docRef = sensorHistoryCollection.document()
                            val sensorDataMap = sensorDataToFirestoreMap(sensorData)
                            firestore.runTransaction { transaction ->
                                transaction.set(docRef, sensorDataMap)
                            }.await()

                            lastFirestoreSaveTime = currentTime
                            lastUploadedTimestamp = sensorData.timestamp
                            lastUploadedSensorData = sensorData
                            saveLastFirestoreTimestamps()
                            Log.d(tagSensorDataRepository, "Saved sensor data to Firestore: $sensorDataMap")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tagSensorDataRepository, "Failed to save sensor data to Firestore", e)
                }
            }
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

                do {
                    val snapshot = if (lastVisibleDocument == null) {
                        query.get().await()
                    } else {
                        query.startAfter(lastVisibleDocument).get().await()
                    }

                    // Map each document manually, excluding waterLevel if present
                    snapshot.documents.forEach { document ->
                        val data = document.data ?: return@forEach

                        // Create a SensorData object, excluding waterLevel if present in Firestore
                        val sensorData = SensorData(
                            carbonDioxide = data["carbonDioxide"] as? Double ?: 0.0,
                            humidity = data["humidity"] as? Double ?: 0.0,
                            lightLevel = data["lightLevel"] as? Double ?: 0.0,
                            pH = data["pH"] as? Double ?: 0.0,
                            temperature = data["temperature"] as? Double ?: 0.0,
                            timestamp = data["timestamp"] as? Long ?: 0L,
                            waterLevel = false, // Default to false or a suitable default
                            waterTemp = data["waterTemp"] as? Double ?: 0.0,
                            tds = data["tds"] as? Double ?: 0.0
                        )
                        results.add(sensorData)
                    }

                    lastVisibleDocument = snapshot.documents.lastOrNull()
                } while (snapshot.size() >= 100)

                results
            } catch (e: Exception) {
                Log.e(tagSensorDataRepository, "Error fetching sensor data for time range", e)
                emptyList()
            }
        }
    }
}