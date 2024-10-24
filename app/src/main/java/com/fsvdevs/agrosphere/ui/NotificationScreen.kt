package com.fsvdevs.agrosphere.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fsvdevs.agrosphere.models.NotificationData
import com.fsvdevs.agrosphere.repository.NotificationRepository
import com.fsvdevs.agrosphere.ui.theme.AgroSphereTheme
import com.fsvdevs.agrosphere.ui.theme.AppTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotificationsScreen() {
    val context = LocalContext.current
    val notificationRepository = NotificationRepository.getInstance(context)

    // Use MutableState for notifications
    val notifications = remember { mutableStateOf(emptyList<NotificationData>()) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    // Coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()

    // Use LaunchedEffect to fetch notifications
    LaunchedEffect(Unit) {
        notifications.value = notificationRepository.getNotifications()
    }

    AgroSphereTheme(isSystemInDarkTheme()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding()
        ) {
            // Confirmation dialog for deletion
            if (showDeleteConfirmation.value) {
                ConfirmationDialog(
                    coroutineScope = coroutineScope,
                    showDeleteConfirmation = showDeleteConfirmation,
                    notificationRepository = notificationRepository,
                    notifications = notifications
                )
            }

            // Display notifications or a message if none available
            if (notifications.value.isEmpty()) {
                Text(
                    text = "No notifications available",
                    style = AppTypography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Historical Notifications",
                                style = MaterialTheme.typography.titleLarge.copy(),
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.wrapContentWidth(),
                                textAlign = TextAlign.Left
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            OutlinedButton(
                                onClick = { showDeleteConfirmation.value = true },
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Text(
                                    text = "Delete All Notifications",
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        notifications.value.forEach { notification ->
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = notification.title,
                                    textAlign = TextAlign.Start,
                                    style = AppTypography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth().weight(.4f)
                                )
                                Spacer(modifier = Modifier.weight(.2f))
                                Text(
                                    text = formatNotificationTimestamp(notification.timestamp),
                                    textAlign = TextAlign.End,
                                    style = AppTypography.bodySmall,
                                    modifier = Modifier.fillMaxWidth().weight(.4f)
                                )
                            }
                            Text(
                                text = notification.message,
                                textAlign = TextAlign.Start,
                                style = AppTypography.bodySmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmationDialog(
    coroutineScope: CoroutineScope,
    showDeleteConfirmation: MutableState<Boolean>,
    notificationRepository: NotificationRepository,
    notifications: MutableState<List<NotificationData>>
) {
    AlertDialog(
        onDismissRequest = { showDeleteConfirmation.value = false },
        title = { Text("Delete All Notifications") },
        text = { Text("Are you sure you want to delete all notifications?") },
        confirmButton = {
            Button(
                onClick = {
                    // Call clearNotifications in a coroutine
                    coroutineScope.launch {
                        notificationRepository.clearNotifications() // Clear notifications
                        notifications.value = emptyList() // Clear the list in UI
                    }
                    showDeleteConfirmation.value = false // Close the dialog
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = { showDeleteConfirmation.value = false }) {
                Text("No")
            }
        }
    )

}

private fun formatNotificationTimestamp(timestamp: Long): String {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    return dateTime.format(formatter)
}