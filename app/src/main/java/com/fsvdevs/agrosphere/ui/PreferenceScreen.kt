package com.fsvdevs.agrosphere.ui

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fsvdevs.agrosphere.BuildConfig
import com.fsvdevs.agrosphere.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    navController: NavController,
    currentTheme: AppTheme,
    dynamicColorEnabled: Boolean,
    onThemeChange: (AppTheme) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    saveThemePreference: (AppTheme) -> Unit,
    saveDynamicColorPreference: (Boolean) -> Unit,
) {
    var showThemeMenu by remember { mutableStateOf(false) }
    var showAccountInfo by rememberSaveable { mutableStateOf(false) }
    var showAppInfo by rememberSaveable { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val email = user?.email ?: "N/A"
    val userId = user?.uid ?: "N/A"

    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE
    val buildType = BuildConfig.BUILD_TYPE
    val applicationId = BuildConfig.APPLICATION_ID

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Theme Section
        Text("Display", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        PreferenceItem("Choose Theme") {
            Box {
                TextButton(onClick = { showThemeMenu = true }) {
                    Text(selectedTheme.name, style = MaterialTheme.typography.bodyLarge)
                }
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

        // Dynamic Color Toggle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PreferenceItem("Use Dynamic Color") {
                Switch(
                    checked = dynamicColorEnabled,
                    onCheckedChange = {
                        onDynamicColorChange(it)
                        saveDynamicColorPreference(it)
                    }
                )
            }
        }

        // Notifications Section
        Spacer(modifier = Modifier.height(16.dp))
        Text("Notifications", style = MaterialTheme.typography.labelMedium)
        PreferenceItem("Enable Notifications") {
            // Add Switch or Checkbox for notifications here if applicable
        }

        // Account Info Section (Expandable)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Account", style = MaterialTheme.typography.labelMedium)
        PreferenceItem("Account Info") {
            IconButton(onClick = { showAccountInfo = !showAccountInfo }) {
                Icon(
                    imageVector = if (showAccountInfo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand"
                )
            }
        }
        AnimatedVisibility(visible = showAccountInfo) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text("Email: $email", style = MaterialTheme.typography.bodySmall)
                Text("User ID: $userId", style = MaterialTheme.typography.bodySmall)
            }
        }

        // App Information Section (Expandable)
        PreferenceItem("App Information") {
            IconButton(onClick = { showAppInfo = !showAppInfo }) {
                Icon(
                    imageVector = if (showAppInfo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand"
                )
            }
        }
        AnimatedVisibility(visible = showAppInfo) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text("AgroSphere: Developed by FSV Developers", style = MaterialTheme.typography.bodySmall)
                Text("Version Name: $versionName", style = MaterialTheme.typography.bodySmall)
                Text("Version Code: $versionCode", style = MaterialTheme.typography.bodySmall)
                Text("Build Type: $buildType", style = MaterialTheme.typography.bodySmall)
                Text("Application ID: $applicationId", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Sign Out Button
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Sign Out", color = MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
fun PreferenceItem(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        content()
    }
}