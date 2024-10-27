package com.fsvdevs.agrosphere.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PrivacyDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.verticalScroll(scrollState).padding(24.dp).fillMaxWidth(),
                ) {
                    Text(
                        text = "Terms of Service and Privacy Policy",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Terms of Service Section
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Left,
                    )

                    Text(
                        text = "1. Acceptance of Terms\n" +
                                "By accessing or using our application, you agree to comply with and be bound by these Terms of Service.\n\n" +
                                "2. Service Description\n" +
                                "Our application provides a platform for monitoring and controlling environmental conditions in a hydroponic greenhouse.\n\n" +
                                "3. User Responsibilities\n" +
                                "- Eligibility: You must be at least 18 years old to use this application.\n" +
                                "- Account Security: You are responsible for maintaining the confidentiality of your account information.\n\n" +
                                "4. User Data\n" +
                                "We collect and process personal data as described in our Privacy Policy.\n\n" +
                                "5. Intellectual Property\n" +
                                "All content and functionalities of the application are owned by us and protected by copyright laws.\n\n" +
                                "6. Limitation of Liability\n" +
                                "We shall not be liable for any indirect or consequential damages arising out of your use of the application.\n\n" +
                                "7. Termination\n" +
                                "We reserve the right to terminate or suspend your access to the application at any time.\n\n" +
                                "8. Changes to the Terms\n" +
                                "We may revise these Terms of Service from time to time.\n\n" +
                                "9. Governing Law\n" +
                                "These Terms shall be governed by the laws of the jurisdiction in which the application is developed.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Left,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Privacy Policy Section
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Left,
                    )

                    Text(
                        text = "1. Introduction\n" +
                                "Your privacy is important to us. This Privacy Policy outlines how we collect, use, and protect your personal information.\n\n" +
                                "2. Information We Collect\n" +
                                "- Personal Data: We may collect personal data such as your name and email address.\n" +
                                "- Sensor Data: The application collects data from various sensors to monitor greenhouse conditions.\n\n" +
                                "3. How We Use Your Information\n" +
                                "We use your information to provide and improve our services, monitor usage trends, and communicate with you.\n\n" +
                                "4. Data Retention\n" +
                                "We will retain your personal data only for as long as necessary for the purposes set out in this Privacy Policy.\n\n" +
                                "5. Data Security\n" +
                                "We implement reasonable security measures to protect your personal data from unauthorized access.\n\n" +
                                "6. Sharing Your Information\n" +
                                "We do not sell or rent your personal information to third parties.\n\n" +
                                "7. User Rights\n" +
                                "You have the right to access, correct, or delete your personal information.\n\n" +
                                "8. Changes to This Privacy Policy\n" +
                                "We may update our Privacy Policy from time to time.\n\n" +
                                "9. Contact Us\n" +
                                "For questions or concerns, please contact the representative via E-Mail: yes@proton.me.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Left,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Go back")
                }
            }
        }
    }
}