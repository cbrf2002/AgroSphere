package com.fsvdevs.agrosphere.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fsvdevs.agrosphere.util.TextLogo

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val splashWidth = LocalConfiguration.current.screenWidthDp.dp
        val logoSize = splashWidth * 0.6f

        TextLogo(modifier = Modifier.size(logoSize))

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "IoT-Based Automated Greenhouse\nMonitoring System",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}