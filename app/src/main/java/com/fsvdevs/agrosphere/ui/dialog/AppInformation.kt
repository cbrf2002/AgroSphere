package com.fsvdevs.agrosphere.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fsvdevs.agrosphere.BuildConfig
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.util.ContentText
import com.fsvdevs.agrosphere.util.TextLogo

@Composable
fun AppInformation() {
    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE

    var showPrivacyDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogoText()
            ContentText("Version $versionName ($versionCode)")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "For the Capstone research entitled:\n\"Implementation of IoT-Based Automated Greenhouse Monitoring System with Internal Climate Control to a Hydroponic Greenhouse in Sampaloc II, Dasmariñas, Cavite\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppAuthors()
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { showPrivacyDialog.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "View Privacy Policy and Terms of Service")
            }

            if (showPrivacyDialog.value) {
                PrivacyDialog(
                    onDismiss = { showPrivacyDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun AppLogoText() {
    Image(
        painter = painterResource(id = R.drawable.agrosphere),
        modifier = Modifier.height(100.dp),
        contentDescription = "AgroSphere Logo"
    )
    Spacer(modifier = Modifier.height(16.dp))
    TextLogo()
    Text(
        text = "IoT-Based Automated Greenhouse\nMonitoring System",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun AppAuthors() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Authors:",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp)) // Add some space

        // Author 1
        AuthorItem(
            name = "Charles Bryan R. Fabian",
            roles = listOf(
                "Project Lead",
                "Software Lead",
                "Database and Communication Protocols",
                "Paper"
            )
        )

        Spacer(modifier = Modifier.height(8.dp)) // Add some space

        // Author 2
        AuthorItem(
            name = "Jerson S. Sumalinog",
            roles = listOf(
                "Deployment Lead",
                "Evaluation",
                "Documentation"
            )
        )

        Spacer(modifier = Modifier.height(8.dp)) // Add some space

        // Author 3
        AuthorItem(
            name = "Paul John S. Vicente",
            roles = listOf(
                "Hardware Lead",
                "Systems Testing"
            )
        )
    }
}

@Composable
fun AuthorItem(name: String, roles: List<String>) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Start
        )
        roles.forEach { role ->
            Text(
                text = "• $role",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}