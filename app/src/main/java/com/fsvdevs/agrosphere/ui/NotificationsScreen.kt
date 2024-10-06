package com.fsvdevs.agrosphere.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.fsvdevs.agrosphere.ui.theme.AgroSphereTheme
import com.fsvdevs.agrosphere.ui.theme.AppTypography

@Composable
fun NotificationsScreen(navController: NavController) {
    AgroSphereTheme(isSystemInDarkTheme()) {
        // State to manage the selected item in the navigation bar
        var selectedItem by remember { mutableStateOf(0) }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Notifications!",
                style = AppTypography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(navController = rememberNavController())
}