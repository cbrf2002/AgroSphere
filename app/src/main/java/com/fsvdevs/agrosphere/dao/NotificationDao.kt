package com.fsvdevs.agrosphere.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fsvdevs.agrosphere.models.NotificationData

@Dao
interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: NotificationData)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    suspend fun getAllNotifications(): List<NotificationData>

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}