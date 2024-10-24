package com.fsvdevs.agrosphere.repository

import android.content.Context
import com.fsvdevs.agrosphere.models.NotificationData
import com.fsvdevs.agrosphere.room.NotificationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository private constructor(context: Context) {

    private val notificationDao = NotificationDatabase.getDatabase(context).notificationDao()

    suspend fun saveNotification(notification: NotificationData) {
        withContext(Dispatchers.IO) {
            notificationDao.insertNotification(notification)
        }
    }

    suspend fun getNotifications(): List<NotificationData> {
        return withContext(Dispatchers.IO) {
            notificationDao.getAllNotifications()
        }
    }

    suspend fun clearNotifications() {
        withContext(Dispatchers.IO) {
            notificationDao.clearAllNotifications()
        }
    }

    companion object {
        @Volatile
        private var instance: NotificationRepository? = null

        fun getInstance(context: Context): NotificationRepository {
            return instance ?: synchronized(this) {
                val newInstance = NotificationRepository(context)
                instance = newInstance
                newInstance
            }
        }
    }
}