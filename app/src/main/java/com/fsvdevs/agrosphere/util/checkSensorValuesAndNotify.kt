package com.fsvdevs.agrosphere.util

import android.content.Context
import com.fsvdevs.agrosphere.models.NotificationData
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.models.SensorRangeData
import com.fsvdevs.agrosphere.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val notificationCooldowns = mutableMapOf<String, Long>()

fun checkSensorValuesAndNotify(context: Context, sensorData: SensorData, sensorRangeData: SensorRangeData) {
    val thresholds = listOf(
        ThresholdCheck("CO2", sensorData.carbonDioxide > 1000.0, "High CO2 Levels", "CO2 levels have exceeded 1000.0 ppm!"),
        ThresholdCheck("Temperature", sensorData.temperature < sensorRangeData.tempRangeLow || sensorData.temperature > sensorRangeData.tempRangeHigh, "Temperature Out of Range", "Temperature is %.2f°C. It should be between %.2f and %.2f.".format(sensorData.temperature, sensorRangeData.tempRangeLow, sensorRangeData.tempRangeHigh)),
        ThresholdCheck("Humidity", sensorData.humidity < sensorRangeData.humRangeLow || sensorData.humidity > sensorRangeData.humRangeHigh, "Humidity Out of Range", "Humidity is %.2f%%. It should be between %.2f and %.2f.".format(sensorData.humidity, sensorRangeData.humRangeLow, sensorRangeData.humRangeHigh)),
        ThresholdCheck("pH", sensorData.pH < 5.5 || sensorData.pH > 6.5, "pH Level Out of Range", "pH levels are %.2f. It should be between 5.5 and 6.5.".format(sensorData.pH)),
        ThresholdCheck("WaterLevel", sensorData.waterLevel == false, "Low Water Level", "Water level is insufficient. Immediate action is required."),
        ThresholdCheck("LightLevel", sensorData.lightLevel < 30000.0, "Insufficient Light", "Light levels are below %.2f lux.".format(sensorData.lightLevel))
    )

    val currentTime = System.currentTimeMillis()
    val notificationRepository = NotificationRepository.getInstance(context)
    val triggeredAlerts = mutableListOf<String>()  // Collect triggered alerts

    thresholds.forEach { threshold ->
        if (threshold.condition && canNotify(threshold.key)) {
            triggeredAlerts.add(threshold.message)

            // Update the last notification time for this condition
            notificationCooldowns[threshold.key] = currentTime

            // Save the notification in the Room database using a coroutine
            CoroutineScope(Dispatchers.IO).launch {
                notificationRepository.saveNotification(NotificationData(title = threshold.title, message = threshold.message, timestamp = currentTime))
            }
        }
    }

    if (triggeredAlerts.isNotEmpty()) {
        // Create a single notification with all triggered alerts
        val combinedMessage = triggeredAlerts.joinToString(separator = "\n")
        NotificationHelper.sendNotification(context, "Sensor Alerts", combinedMessage)
        NotificationHelper.showGroupSummaryNotification(context)
    }
}

private data class ThresholdCheck(
    val key: String,
    val condition: Boolean,
    val title: String,
    val message: String
)

private fun canNotify(condition: String): Boolean {
    val cooldownTime = 900000 // 15 minutes in milliseconds
    val lastNotificationTime = notificationCooldowns[condition]
    return lastNotificationTime == null || (System.currentTimeMillis() - lastNotificationTime) > cooldownTime
}