package com.fsvdevs.agrosphere.ui

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.fsvdevs.agrosphere.ui.dialog.AppInformation
import com.fsvdevs.agrosphere.ui.theme.AppTheme
import com.fsvdevs.agrosphere.util.ContentText
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    currentTheme: AppTheme,
    dynamicColorEnabled: Boolean,
    onThemeChange: (AppTheme) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    saveThemePreference: (AppTheme) -> Unit,
    saveDynamicColorPreference: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        DisplaySettings(
            currentTheme = currentTheme,
            onThemeChange = onThemeChange,
            saveThemePreference = saveThemePreference,
            dynamicColorEnabled = dynamicColorEnabled,
            onDynamicColorChange = onDynamicColorChange,
            saveDynamicColorPreference = saveDynamicColorPreference
        )

        HorizontalDivider()
        NotificationSettings()
        HorizontalDivider()

        AccountSettings()
        HorizontalDivider()

        AboutUsSection()
        HorizontalDivider()
    }
}

@Composable
fun PreferenceHeaders(label: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        label,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun PreferenceItem(
    label: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        content() // Call the content composable here
    }
}

@Composable
fun DisplaySettings(
    currentTheme: AppTheme,
    dynamicColorEnabled: Boolean,
    onThemeChange: (AppTheme) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    saveThemePreference: (AppTheme) -> Unit,
    saveDynamicColorPreference: (Boolean) -> Unit
) {
    var showThemeMenu by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    PreferenceHeaders("Display")
    PreferenceItem("Choose Theme", onClick = { showThemeMenu = true }) {
        Box {
            Text(selectedTheme.name, style = MaterialTheme.typography.bodyLarge)
            DropdownMenu(
                expanded = showThemeMenu,
                onDismissRequest = { showThemeMenu = false }
            ) {
                AppTheme.entries.forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(theme.name) },
                        onClick = {
                            onThemeChange(theme)
                            selectedTheme = theme
                            saveThemePreference(theme)
                            showThemeMenu = false
                        }
                    )
                }
            }
        }
    }

    HorizontalDivider()

    // Dynamic Color Toggle
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PreferenceItem("Use Dynamic Color", onClick = {
            // Handle click event to toggle the switch or navigate
            onDynamicColorChange(!dynamicColorEnabled)
            saveDynamicColorPreference(!dynamicColorEnabled)
        }) {
            Switch(
                checked = dynamicColorEnabled,
                onCheckedChange = {
                    onDynamicColorChange(it)
                    saveDynamicColorPreference(it)
                }
            )
        }
    }
}

@Composable
fun NotificationSettings() {
    PreferenceHeaders("Notifications")
    PreferenceItem("Enable Notifications", onClick = {
        // Handle click event for notifications, e.g., toggle a switch
    }) {
        // Add Switch or Checkbox for notifications here if applicable
    }
}

@Composable
fun AccountSettings() {
    var showAccountInfo by rememberSaveable { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val email = user?.email ?: "N/A"
    val userId = user?.uid ?: "N/A"

    PreferenceHeaders("Account")
    PreferenceItem("Account Info", onClick = { showAccountInfo = !showAccountInfo }) {
        // Animate rotation
        val rotationState by animateFloatAsState(
            targetValue = if (showAccountInfo) 180f else 0f,
            animationSpec = tween(durationMillis = 300), label = "" // Adjust duration as needed
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Expand",
            modifier = Modifier.rotate(rotationState)
        )
    }
    AnimatedVisibility(visible = showAccountInfo) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 24.dp)
        ) {
            ContentText("Email: $email")
            ContentText("User ID: $userId")
        }
    }

    HorizontalDivider()

    PreferenceItem(label = "Sign Out", onClick = {
        FirebaseAuth.getInstance().signOut()
    }) {
        // Optional: Add an icon or additional UI if desired
    }
}

@Composable
fun AboutUsSection() {
    var showAppInfo by rememberSaveable { mutableStateOf(false) }
    PreferenceHeaders("Information")
    PreferenceItem("About Us", onClick = { showAppInfo = !showAppInfo }) {
        IconButton(onClick = { showAppInfo = !showAppInfo }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "Expand"
            )
        }
    }
    if (showAppInfo) {
        Dialog(
            onDismissRequest = { showAppInfo = false }
        ) {
            AppInformation()
        }
    }
}