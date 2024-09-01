package com.fsvdevs.agrosphere

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fsvdevs.agrosphere.ui.theme.AgroSphereTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            val navController = rememberNavController()
            var selectedItem by remember { mutableStateOf(Routes.DASHBOARD_SCREEN) }

            AgroSphereTheme(isDarkTheme) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedItem == Routes.DASHBOARD_SCREEN,
                                onClick = {
                                    selectedItem = Routes.DASHBOARD_SCREEN
                                    navController.navigate(Routes.DASHBOARD_SCREEN) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                },
                                label = { Text("Dashboard") },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") }
                            )
                            NavigationBarItem(
                                selected = selectedItem == Routes.MONITOR_SCREEN,
                                onClick = {
                                    selectedItem = Routes.MONITOR_SCREEN
                                    navController.navigate(Routes.MONITOR_SCREEN) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                },
                                label = { Text("Monitor") },
                                icon = { Icon(Icons.Filled.ThumbUp, contentDescription = "Monitor") }
                            )
                            NavigationBarItem(
                                selected = selectedItem == Routes.PREFERENCES_SCREEN,
                                onClick = {
                                    selectedItem = Routes.PREFERENCES_SCREEN
                                    navController.navigate(Routes.PREFERENCES_SCREEN) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                },
                                label = { Text("Preferences") },
                                icon = { Icon(Icons.Filled.Settings, contentDescription = "Preferences") }
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.DASHBOARD_SCREEN,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Routes.DASHBOARD_SCREEN) { dashboardScreen(navController) }
                        composable(Routes.MONITOR_SCREEN) { monitorScreen(navController) }
                        composable(Routes.PREFERENCES_SCREEN) { preferencesScreen(navController) }
                    }
                }
            }
        }
    }
}
