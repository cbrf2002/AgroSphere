package com.fsvdevs.agrosphere.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.fsvdevs.agrosphere.models.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

class SensorDataRepository(
    private val database: DatabaseReference,
    private val context: Context
) {
    // Constants declaration
    private val tagSensorDataRepository = "SensorDataRepository"
    private var lastDataChangeTime: Long = 0  // Store the last data change time
    
    // Shared Preferences setup
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "sensor_history_cache", Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val lastTimestampKey = "last_data_timestamp"
    private val cacheVersionKey = "cache_version"
    private val historyCachePrefix = "history_cache_"
    
    // Memory cache for recently loaded ranges
    private val memoryCache = ConcurrentHashMap<String, Pair<List<SensorData>, Long>>()
    
    // Track if database structure might have changed
    private val _databaseChangeDetected = MutableStateFlow(false)
    val databaseChangeDetected: StateFlow<Boolean> = _databaseChangeDetected

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData

    // Add a listener for sensorHistory node to detect changes including deletions
    private val historyChangeListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // When data in sensorHistory changes, mark cache as potentially stale
            _databaseChangeDetected.value = true
            
            // Invalidate memory cache immediately
            memoryCache.clear()
            Log.d(tagSensorDataRepository, "Database change detected, memory cache cleared")
            
            // Update cache version to invalidate shared preferences cache
            val currentVersion = getCurrentCacheVersion()
            saveCacheVersion(currentVersion + 1)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(tagSensorDataRepository, "History listener cancelled", error.toException())
        }
    }

    private val sensorDataListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastDataChangeTime >= 5000) { // Reduced cooldown to 5 seconds
                if (snapshot.exists()) {
                    val data = snapshot.getValue(SensorData::class.java)
                    data?.let {
                        if (isCompleteSensorData(it)) {
                            _sensorData.value = it
                            saveLastDataTimestamp(it.timestamp)
                        }
                    }
                }
                lastDataChangeTime = currentTime
            }
        }

        private fun isCompleteSensorData(sensorData: SensorData): Boolean {
            return sensorData.humidity >= 0.0 &&
                    sensorData.lightLevel >= 0.0 &&
                    sensorData.pH >= 0.0 &&
                    sensorData.temperature >= -20.0 &&
                    sensorData.waterTemp >= 0.0 &&
                    sensorData.tds >= 0.0
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(tagSensorDataRepository, "Failed to fetch sensor data", error.toException())
        }
    }

    init {
        startListeningForSensorData()
        startListeningForHistoryChanges()
    }

    private fun startListeningForSensorData() {
        database.child("sensorData").addValueEventListener(sensorDataListener)
    }
    
    private fun startListeningForHistoryChanges() {
        database.child("sensorHistory").limitToLast(1).addValueEventListener(historyChangeListener)
    }

    fun stopListeningForSensorData() {
        database.child("sensorData").removeEventListener(sensorDataListener)
        database.child("sensorHistory").removeEventListener(historyChangeListener)
    }

    suspend fun getSensorHistoryByTimePeriod(periodInMillis: Long, forceRefresh: Boolean = false): List<SensorData> {
        val timeRangeKey = getTimeRangeKey(periodInMillis)
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - periodInMillis
        
        // If database changes were detected or force refresh is requested, bypass cache
        if (forceRefresh || _databaseChangeDetected.value) {
            Log.d(tagSensorDataRepository, "Force refresh or database change detected, bypassing cache")
            _databaseChangeDetected.value = false
            return fetchDataFromFirebase(startTime, currentTime, timeRangeKey)
        }
        
        // Check if cache version matches current version
        val cacheVersion = getCurrentCacheVersion()
        val cachedVersion = sharedPreferences.getLong("${timeRangeKey}_version", -1)
        if (cachedVersion != cacheVersion) {
            Log.d(tagSensorDataRepository, "Cache version mismatch for $timeRangeKey: $cachedVersion vs $cacheVersion")
            return fetchDataFromFirebase(startTime, currentTime, timeRangeKey)
        }
        
        // Check memory cache first (most efficient)
        val memoryCacheEntry = memoryCache[timeRangeKey]
        if (memoryCacheEntry != null && (currentTime - memoryCacheEntry.second) < 60000) { // 1 minute cache
            Log.d(tagSensorDataRepository, "Using memory cache for $timeRangeKey")
            return memoryCacheEntry.first
        }
        
        // Then check shared preferences cache
        val cachedData = getCachedHistoryData(timeRangeKey)
        if (cachedData != null) {
            // Determine if cache is still fresh - use shorter timeout
            val lastTimestamp = getLastDataTimestamp()
            val newestCachedTimestamp = cachedData.maxByOrNull { it.timestamp }?.timestamp ?: 0L
            
            // If we have cached data and no new data has arrived since last cache
            if (newestCachedTimestamp >= lastTimestamp - 30000) { // Allow 30 seconds difference
                Log.d(tagSensorDataRepository, "Using preference cache for $timeRangeKey")
                
                // Update memory cache and return
                memoryCache[timeRangeKey] = Pair(cachedData, currentTime)
                return cachedData
            }
            
            // Try incremental fetch to keep data fresh
            try {
                val incrementalData = fetchIncrementalData(newestCachedTimestamp, currentTime)
                if (incrementalData.isNotEmpty()) {
                    // Combine old and new data
                    val combinedData = (cachedData + incrementalData)
                        .distinctBy { it.timestamp }
                        .sortedBy { it.timestamp }
                        
                    // Clean up old data outside the time range
                    val updatedData = combinedData.filter { it.timestamp >= startTime }
                    
                    // Cache and return
                    cacheHistoryData(timeRangeKey, updatedData)
                    memoryCache[timeRangeKey] = Pair(updatedData, currentTime)
                    
                    Log.d(tagSensorDataRepository, "Used incremental fetch, combined with cache for $timeRangeKey")
                    return updatedData
                }
            } catch (e: Exception) {
                Log.w(tagSensorDataRepository, "Incremental fetch failed: ${e.message}")
            }
        }
        
        // If we reach here, we need to fetch from Firebase
        Log.d(tagSensorDataRepository, "Performing full fetch from Firebase for $timeRangeKey")
        return fetchDataFromFirebase(startTime, currentTime, timeRangeKey)
    }
    
    // Force invalidate all caches - should be called when data structure changes
    fun invalidateAllCaches() {
        Log.d(tagSensorDataRepository, "Invalidating all caches")
        memoryCache.clear()
        val currentVersion = getCurrentCacheVersion()
        saveCacheVersion(currentVersion + 1)
        _databaseChangeDetected.value = true
    }
    
    private fun getCurrentCacheVersion(): Long {
        return sharedPreferences.getLong(cacheVersionKey, 1)
    }
    
    private fun saveCacheVersion(version: Long) {
        sharedPreferences.edit().putLong(cacheVersionKey, version).apply()
        Log.d(tagSensorDataRepository, "Cache version updated to $version")
    }

    private suspend fun fetchIncrementalData(lastKnownTimestamp: Long, currentTime: Long): List<SensorData> {
        return withContext(Dispatchers.IO) {
            try {
                // Query only data newer than what we already have
                val query: Query = database.child("sensorHistory")
                    .orderByChild("timestamp")
                    .startAfter(lastKnownTimestamp.toDouble())
                    .endAt(currentTime.toDouble())
                    .limitToLast(100) // Limit to avoid too much data
                    
                val result = mutableListOf<SensorData>()
                val snapshot = query.get().await()
                
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val sensorData = childSnapshot.getValue(SensorData::class.java)
                        sensorData?.let { result.add(it) }
                    }
                }
                result
            } catch (e: Exception) {
                Log.e(tagSensorDataRepository, "Error in incremental fetch", e)
                emptyList<SensorData>()
            }
        }
    }

    private suspend fun fetchDataFromFirebase(startTime: Long, endTime: Long, timeRangeKey: String): List<SensorData> {
        return withContext(Dispatchers.IO) {
            try {
                // First try a more efficient approach with query
                val result = try {
                    fetchWithQuery(startTime, endTime)
                } catch (e: Exception) {
                    Log.w(tagSensorDataRepository, "Query failed, trying fallback method: ${e.message}")
                    // Fallback to getting all data and filtering in-app
                    fetchAllAndFilter(startTime, endTime)
                }
                
                // Sort the result by timestamp in ascending order
                result.sortBy { it.timestamp }
                
                Log.d(tagSensorDataRepository, "Fetched ${result.size} historical data points from Realtime Database")
                
                // Save to both caches
                cacheHistoryData(timeRangeKey, result)
                memoryCache[timeRangeKey] = Pair(result, System.currentTimeMillis())
                
                result
            } catch (e: Exception) {
                Log.e(tagSensorDataRepository, "Error fetching sensor history from Realtime Database", e)
                // Return cached data if available when fetch fails
                getCachedHistoryData(timeRangeKey) ?: emptyList()
            }
        }
    }
    
    private suspend fun fetchWithQuery(startTime: Long, endTime: Long): MutableList<SensorData> {
        val query: Query = database.child("sensorHistory")
            .orderByChild("timestamp")
            .startAt(startTime.toDouble())
            .endAt(endTime.toDouble())
            .limitToLast(500)
            
        val result = mutableListOf<SensorData>()
        val snapshot = query.get().await()
        
        if (snapshot.exists()) {
            for (childSnapshot in snapshot.children) {
                val sensorData = childSnapshot.getValue(SensorData::class.java)
                sensorData?.let {
                    result.add(it)
                }
            }
        }
        return result
    }
    
    private suspend fun fetchAllAndFilter(startTime: Long, endTime: Long): MutableList<SensorData> {
        // Get all data and filter client-side (less efficient but works without index)
        val query: Query = database.child("sensorHistory")
            .limitToLast(500) // Still limit to prevent huge downloads
            
        val result = mutableListOf<SensorData>()
        val snapshot = query.get().await()
        
        if (snapshot.exists()) {
            for (childSnapshot in snapshot.children) {
                val sensorData = childSnapshot.getValue(SensorData::class.java)
                sensorData?.let {
                    // Client-side filtering by timestamp
                    if (it.timestamp in startTime..endTime) {
                        result.add(it)
                    }
                }
            }
            
            // Special handling for hour view - if we have very few data points,
            // we might want to include at least a minimum number
            if (endTime - startTime <= 3600000L && result.size < 5) {
                // For hour view with few points, get at least the 10 most recent entries
                val allData = mutableListOf<SensorData>()
                for (childSnapshot in snapshot.children) {
                    val sensorData = childSnapshot.getValue(SensorData::class.java)
                    sensorData?.let {
                        allData.add(it)
                    }
                }
                
                // Sort by timestamp and take the most recent ones
                if (allData.size > result.size) {
                    allData.sortByDescending { it.timestamp }
                    return allData.take(10).asReversed().toMutableList()
                }
            }
        }
        return result
    }
    
    // Shared preferences caching methods
    private fun cacheHistoryData(timeRangeKey: String, data: List<SensorData>) {
        val json = gson.toJson(data)
        val cacheVersion = getCurrentCacheVersion()
        sharedPreferences.edit()
            .putString(historyCachePrefix + timeRangeKey, json)
            .putLong("${timeRangeKey}_version", cacheVersion)
            .apply()
        Log.d(tagSensorDataRepository, "Cached ${data.size} entries for $timeRangeKey with version $cacheVersion")
    }
    
    private fun getCachedHistoryData(timeRangeKey: String): List<SensorData>? {
        val json = sharedPreferences.getString(historyCachePrefix + timeRangeKey, null) ?: return null
        val type: Type = object : TypeToken<List<SensorData>>() {}.type
        return gson.fromJson(json, type)
    }
    
    private fun saveLastDataTimestamp(timestamp: Long) {
        val currentLastTimestamp = sharedPreferences.getLong(lastTimestampKey, 0L)
        if (timestamp > currentLastTimestamp) {
            sharedPreferences.edit().putLong(lastTimestampKey, timestamp).apply()
        }
    }
    
    private fun getLastDataTimestamp(): Long {
        return sharedPreferences.getLong(lastTimestampKey, 0L)
    }
    
    private fun getTimeRangeKey(periodInMillis: Long): String {
        return when(periodInMillis) {
            3600000L -> "hour"
            86400000L -> "day"
            604800000L -> "week"
            2629746000L -> "month"
            31556952000L -> "year"
            else -> "custom_${periodInMillis}"
        }
    }
    
    // Clear all cached data - useful when signing out
    fun clearAllCachedData() {
        sharedPreferences.edit().clear().apply()
        memoryCache.clear()
        saveCacheVersion(1L)
        _databaseChangeDetected.value = false
        Log.d(tagSensorDataRepository, "All cached data cleared, cache version reset")
    }
}