package com.fsvdevs.agrosphere.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Room will automatically assign a unique ID
    val title: String,
    val message: String,
    val timestamp: Long
)