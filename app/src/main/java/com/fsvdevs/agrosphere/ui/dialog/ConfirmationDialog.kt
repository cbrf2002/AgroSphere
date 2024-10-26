package com.fsvdevs.agrosphere.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.models.NotificationData
import com.fsvdevs.agrosphere.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ConfirmationDialog(
    coroutineScope: CoroutineScope,
    showDeleteConfirmation: MutableState<Boolean>,
    notificationRepository: NotificationRepository,
    notifications: MutableState<List<NotificationData>>
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.rounded_notifications_off_24),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "Notifications off",
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Delete All Notifications",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Are you sure you want to delete all notifications?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            notificationRepository.clearNotifications()
                            notifications.value = emptyList()
                        }
                        showDeleteConfirmation.value = false
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Yes")
                }
                OutlinedButton(
                    onClick = { showDeleteConfirmation.value = false }
                ) {
                    Text("No")
                }
            }
        }
    }
}