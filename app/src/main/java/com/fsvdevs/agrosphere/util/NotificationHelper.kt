package com.fsvdevs.agrosphere.util

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fsvdevs.agrosphere.MainActivity
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.routes.Routes
import com.fsvdevs.agrosphere.ui.theme.primaryLight

object NotificationHelper {
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private const val CHANNEL_ID = "sensor_notification_channel"
    private const val CHANNEL_NAME = "Sensor Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for sensor threshold alerts"
    private const val NOTIFICATION_GROUP_KEY = "sensor_alerts_group"
    private var notificationIdCounter = 1
    private var lastSummaryNotificationTime: Long = 0

    fun checkAndRequestNotificationPermission(
        activity: Activity,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("NotificationHelper", "Notification permission already granted")
                }
                else -> {
                    Log.d("NotificationHelper", "Requesting notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = CHANNEL_DESCRIPTION
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(context: Context, title: String, message: String) {
        // Check for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            Log.e("NotificationHelper", "Notification permission not granted")
            return
        }

        // Create an Intent to open MainActivity and navigate to NotificationScreen
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", Routes.NOTIFICATIONS_SCREEN)
        }

        // Create a PendingIntent for the notification click action
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification with grouping and pending intent
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.agrosphere_mono)
            .setColor(primaryLight.toArgb())
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(NOTIFICATION_GROUP_KEY)  // Set the group key for the notification
            .setAutoCancel(true)  // Automatically remove the notification when clicked
            .setContentIntent(pendingIntent)  // Set the pending intent

        // Generate a unique notification ID using the counter
        val notificationId = notificationIdCounter++

        // Show the notification with a unique ID
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    }

    fun showGroupSummaryNotification(context: Context) {
        val cooldownTime = 1800000 // 30 minutes in milliseconds
        val currentTime = System.currentTimeMillis()

        // Check for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request permission
            ActivityCompat.requestPermissions(
                (context as Activity), // Cast context to Activity
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE // Define a request code
            )
            return
        }

        // Only show the summary notification if the cooldown has passed
        if (currentTime - lastSummaryNotificationTime > cooldownTime) {
            // Create the summary notification
            val summaryNotificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.agrosphere_mono)
                .setColor(primaryLight.toArgb())
                .setContentTitle("Sensor Alerts")
                .setContentText("You have new sensor alerts.")
                .setStyle(NotificationCompat.InboxStyle()
                    .setBigContentTitle("Sensor Alerts")
                    .addLine("You have alerts from your sensors."))
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setAutoCancel(true) // Automatically remove when clicked

            // Show the summary notification
            NotificationManagerCompat.from(context).notify(0, summaryNotificationBuilder.build())

            // Update the last summary notification time
            lastSummaryNotificationTime = currentTime
        }
    }
}