package com.fsvdevs.agrosphere

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fsvdevs.agrosphere.ui.theme.AgroSphereTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            val navController = rememberNavController()
            var selectedItem by remember { mutableStateOf(Routes.DASHBOARD_SCREEN) }

            if (!isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    )
                )
            } else {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(
                        android.graphics.Color.TRANSPARENT,
                    )
                )
            }

            AgroSphereTheme(isDarkTheme) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    bottomBar = {
                        BottomNavigationBar(
                            selectedItem = selectedItem,
                            onItemSelected = { newItem ->
                                selectedItem = newItem
                                navController.navigate(newItem) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    NavRoutes(navController = navController, modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}

@Composable
fun NavRoutes(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD_SCREEN,
        modifier = modifier
    ) {
        composable(Routes.DASHBOARD_SCREEN) { DashboardScreen(navController) }
        composable(Routes.MONITOR_SCREEN) { MonitorScreen(navController) }
        composable(Routes.PREFERENCES_SCREEN) { PreferencesScreen(navController) }
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == Routes.DASHBOARD_SCREEN,
            onClick = { onItemSelected(Routes.DASHBOARD_SCREEN) },
            label = { Text("Dashboard") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") }
        )
        NavigationBarItem(
            selected = selectedItem == Routes.MONITOR_SCREEN,
            onClick = { onItemSelected(Routes.MONITOR_SCREEN) },
            label = { Text("Monitor") },
            icon = { Icon(Icons.Filled.ThumbUp, contentDescription = "Monitor") }
        )
        NavigationBarItem(
            selected = selectedItem == Routes.PREFERENCES_SCREEN,
            onClick = { onItemSelected(Routes.PREFERENCES_SCREEN) },
            label = { Text("Preferences") },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Preferences") }
        )
    }
}