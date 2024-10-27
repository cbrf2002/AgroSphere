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
            return sensorData.humidity != 0.0 &&
                    sensorData.lightLevel != 0.0 &&
                    sensorData.pH != 0.0 &&
                    sensorData.temperature != 0.0 &&
                    sensorData.waterLevel != 0.0 &&
                    sensorData.waterTemp != 0.0
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
        // Add a delay to allow other operations to complete
        delay(10000) // Delay for 10 seconds

        val currentTime = System.currentTimeMillis()

        // Check if the current sensor data's timestamp is not within 10 minutes of the last uploaded timestamp
        if (sensorData.timestamp - lastUploadedTimestamp >= 600_000) {
            // Check if the new sensor data is different from the last uploaded sensor data
            if (lastUploadedSensorData == null || lastUploadedSensorData != sensorData) {
                try {
                    // Avoid uploading if the same timestamp exists in Firestore
                    if (currentTime - lastFirestoreSaveTime >= firestoreSaveInterval) {
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
                            lastUploadedTimestamp = sensorData.timestamp // Update the last uploaded timestamp
                            lastUploadedSensorData = sensorData // Store the last uploaded sensor data
                            saveLastFirestoreTimestamps() // Save the last timestamps
                            Log.d(tagSensorDataRepository, "Saved sensor data to Firestore: $sensorData")
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
                Log.e(tagSensorDataRepository, "Error fetching sensor data for time range", e)
                emptyList()
            }
        }
    }
}