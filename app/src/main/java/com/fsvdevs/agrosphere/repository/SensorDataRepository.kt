package com.fsvdevs.agrosphere.repository

import android.util.Log
import com.fsvdevs.agrosphere.models.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private val sensorHistoryCollection = firestore.collection("sensorHistory") // Ensure consistency
    private var lastSavedTimestamp: Long? = null

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData

    private val sensorDataListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val data = snapshot.getValue(SensorData::class.java)
                _sensorData.value = data
                Log.d(tag, "Fetched sensor data: $data")

                data?.let {
                    val currentTimestamp = it.timestamp

                    if (currentTimestamp != null && currentTimestamp != lastSavedTimestamp) {
                        lastSavedTimestamp = currentTimestamp
                        CoroutineScope(Dispatchers.IO).launch {
                            saveSensorDataToFirestore(it)
                        }
                    } else {
                        Log.d(tag, "Timestamp hasn't changed, skipping save to Firestore")
                    }
                }
            } else {
                Log.w(tag, "No sensor data found")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(tag, "Failed to fetch sensor data", error.toException())
        }
    }

    // Introduce a delay mechanism to reduce writes to Firestore
    private var lastFirestoreSaveTime: Long = 0
    private val firestoreSaveInterval = 600000 // 10 minutes in milliseconds

    init {
        startListeningForSensorData()
    }

    private fun startListeningForSensorData() {
        database.child("sensorData").addValueEventListener(sensorDataListener)
    }

    private suspend fun saveSensorDataToFirestore(sensorData: SensorData) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFirestoreSaveTime >= firestoreSaveInterval) {
            try {
                // Added: Batched write operation for future optimization
                firestore.runBatch { batch ->
                    val docRef = sensorHistoryCollection.document()
                    batch.set(docRef, sensorData)
                }.await()
                lastFirestoreSaveTime = currentTime
                Log.d(tag, "Saved sensor data to Firestore: $sensorData")
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

    suspend fun getSensorHistoryByRange(timeRange: String): List<SensorData> {
        return when (timeRange) {
            "Hour" -> getDataForLastHour()
            "Day" -> getDataForLastDay()
            "Week" -> getDataForLastWeek()
            "Month" -> getDataForLastMonth()
            "Year" -> getDataForLastYear()
            else -> emptyList()
        }
    }

    // Function to get sensor data for the last hour
    suspend fun getDataForLastHour(): List<SensorData> {
        val currentTime = System.currentTimeMillis()
        val oneHourAgo = currentTime - 3600000 // 1 hour in milliseconds
        return getDataByTimeRange(oneHourAgo, currentTime)
    }

    // Function to get sensor data for the last day
    suspend fun getDataForLastDay(): List<SensorData> {
        val currentTime = System.currentTimeMillis()
        val oneDayAgo = currentTime - 86400000 // 1 day in milliseconds
        return getDataByTimeRange(oneDayAgo, currentTime)
    }

    // Function to get sensor data for the last week
    suspend fun getDataForLastWeek(): List<SensorData> {
        val currentTime = System.currentTimeMillis()
        val oneWeekAgo = currentTime - 604800000 // 1 week in milliseconds
        return getDataByTimeRange(oneWeekAgo, currentTime)
    }

    // Function to get sensor data for the last month
    suspend fun getDataForLastMonth(): List<SensorData> {
        val currentTime = System.currentTimeMillis()
        val oneMonthAgo = currentTime - 2629746000 // Average month in milliseconds
        return getDataByTimeRange(oneMonthAgo, currentTime)
    }

    // Function to get sensor data for the last year
    suspend fun getDataForLastYear(): List<SensorData> {
        val currentTime = System.currentTimeMillis()
        val oneYearAgo = currentTime - 31556952000 // 1 year in milliseconds
        return getDataByTimeRange(oneYearAgo, currentTime)
    }

    // Generic function to get data between two timestamps
    private suspend fun getDataByTimeRange(startTime: Long, endTime: Long): List<SensorData> {
        return withContext(Dispatchers.IO) {
            try {
                val query = firestore.collection("sensorHistory")
                    .whereGreaterThanOrEqualTo("timestamp", startTime)
                    .whereLessThanOrEqualTo("timestamp", endTime)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limit(100)  // Fetch 100 records at a time

                val results = mutableListOf<SensorData>()
                var lastVisibleDocument: com.google.firebase.firestore.DocumentSnapshot? = null

                // Fetch in chunks of 100 documents
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