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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.util.DensityHelper.getScaledDensity

@Composable
fun PrivacyDialog(
    onDismiss: () -> Unit
) {
    CompositionLocalProvider(LocalDensity provides getScaledDensity()) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val scrollState = rememberScrollState()

                    // Scrollable content section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.privacy_policy_and_tos_title),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider()
                        TermsOfServiceScreen()
                        HorizontalDivider()
                        PrivacyPolicyScreen()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dismiss button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text("Go back")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun CustomHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun CustomContent(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun PrivacyPolicyScreen() {
    val collectionInfo = listOf(
        "Your device's Internet Protocol address (e.g. IP address)",
        "The pages of the Application that you visit, the time and date of your visit, the time spent on those pages",
        "The time spent on the Application",
        "The operating system you use on your mobile device"
    )

    val usesOfLocation = listOf(
        "Geolocation Services: The Service Provider utilizes location data to provide features such as personalized content, relevant recommendations, and location-based services.",
        "Analytics and Improvements: Aggregated and anonymized location data helps the Service Provider to analyze user behavior, identify trends, and improve the overall performance and functionality of the Application.",
        "Third-Party Services: Periodically, the Service Provider may transmit anonymized location data to external services. These services assist them in enhancing the Application and optimizing their offerings."
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        CustomTitle(text = "Privacy Policy")

        CustomContent(text = "This privacy policy applies to the AgroSphere app (hereby referred to as \"Application\") for mobile devices that was created by FSVdevs (hereby referred to as \"Service Provider\") as a Commercial service. This service is intended for use \"AS IS\".")

        CustomHeader(text = "Information Collection and Use")
        CustomContent(text = "The Application collects information when you download and use it. This information may include:")
        BulletPointList(items = collectionInfo)

        CustomContent(text = "The Application collects your device's location, which helps the Service Provider determine your approximate geographical location and make use of in below ways:")
        BulletPointList(items = usesOfLocation)

        CustomHeader(text = "Third Party Access")
        CustomContent(text = "Only aggregated, anonymized data is periodically transmitted to external services to aid the Service Provider in improving the Application and their service. The Service Provider may share your information with third parties in the ways that are described in this privacy statement.")

        CustomContent(text = "Please note that the Application utilizes third-party services that have their own Privacy Policy about handling data. Below are the links to the Privacy Policy of the third-party service providers used by the Application:")
        BulletPointList(items = listOf(
            "Google Play Services: https://www.google.com/policies/privacy/",
            "Google Analytics for Firebase: https://firebase.google.com/support/privacy"
        ))

        CustomHeader(text = "Opt-Out Rights")
        CustomContent(text = "You can stop all collection of information by the Application easily by uninstalling it. You may use the standard uninstall processes as may be available as part of your mobile device or via the mobile application marketplace or network.")

        CustomHeader(text = "Data Retention Policy")
        CustomContent(text = "The Service Provider will retain User Provided data for as long as you use the Application and for a reasonable time thereafter. If you'd like them to delete User Provided Data that you have provided via the Application, please contact them at cbrf2002@proton.me and they will respond in a reasonable time.")

        CustomHeader(text = "Children")
        CustomContent(text = "The Service Provider does not use the Application to knowingly solicit data from or market to children under the age of 13. The Application does not address anyone under the age of 13. The Service Provider does not knowingly collect personally identifiable information from children under 13 years of age. In the case the Service Provider discovers that a child under 13 has provided personal information, the Service Provider will immediately delete this from their servers.")

        CustomHeader(text = "Security")
        CustomContent(text = "The Service Provider is concerned about safeguarding the confidentiality of your information. The Service Provider provides physical, electronic, and procedural safeguards to protect information the Service Provider processes and maintains.")

        CustomHeader(text = "Changes")
        CustomContent(text = "This Privacy Policy may be updated from time to time for any reason. The Service Provider will notify you of any changes to the Privacy Policy by updating this page with the new Privacy Policy. You are advised to consult this Privacy Policy regularly for any changes, as continued use is deemed approval of all changes.")

        CustomHeader(text = "Your Consent")
        CustomContent(text = "By using the Application, you are consenting to the processing of your information as set forth in this Privacy Policy now and as amended by us.")

        CustomHeader(text = "Contact Us")
        CustomContent(text = "If you have any questions regarding privacy while using the Application, or have questions about the practices, please contact the Service Provider via email at cbrf2002@proton.me.")

        CustomContent(text = "This privacy policy is effective as of 2024-10-27")
    }
}

@Composable
fun TermsOfServiceScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        CustomTitle(text = "Terms & Conditions")

        CustomContent(text = "These terms and conditions apply to the AgroSphere app (hereby referred to as \"Application\") for mobile devices that was created by FSVdevs (hereby referred to as \"Service Provider\") as a Commercial service.")

        CustomContent(text = "Upon downloading or utilizing the Application, you are automatically agreeing to the following terms. It is strongly advised that you thoroughly read and understand these terms prior to using the Application.")

        CustomContent(text = "Unauthorized copying, modification of the Application, any part of the Application, or our trademarks is strictly prohibited. Any attempts to extract the source code of the Application, translate the Application into other languages, or create derivative versions are not permitted. All trademarks, copyrights, database rights, and other intellectual property rights related to the Application remain the property of the Service Provider.")

        CustomContent(text = "The Service Provider is dedicated to ensuring that the Application is as beneficial and efficient as possible. As such, they reserve the right to modify the Application or charge for their services at any time and for any reason. The Service Provider assures you that any charges for the Application or its services will be clearly communicated to you.")

        CustomContent(text = "The Application stores and processes personal data that you have provided to the Service Provider in order to provide the Service. It is your responsibility to maintain the security of your phone and access to the Application. The Service Provider strongly advises against jailbreaking or rooting your phone, which involves removing software restrictions and limitations imposed by the official operating system of your device. Such actions could expose your phone to malware, viruses, malicious programs, compromise your phone's security features, and may result in the Application not functioning correctly or at all.")

        CustomContent(text = "Please note that the Application utilizes third-party services that have their own Terms and Conditions. Below are the links to the Terms and Conditions of the third-party service providers used by the Application:")
        BulletPointList(items = listOf(
            "Google Play Services: https://play.google.com/about/play-terms/index.html",
            "Google Analytics for Firebase: https://firebase.google.com/support/terms"
        ))

        CustomContent(text = "Please be aware that the Service Provider does not assume responsibility for certain aspects. Some functions of the Application require an active internet connection, which can be Wi-Fi or provided by your mobile network provider. The Service Provider cannot be held responsible if the Application does not function at full capacity due to lack of access to Wi-Fi or if you have exhausted your data allowance.")

        CustomContent(text = "If you are using the application outside of a Wi-Fi area, please be aware that your mobile network provider's agreement terms still apply. Consequently, you may incur charges from your mobile provider for data usage during the connection to the application, or other third-party charges. By using the application, you accept responsibility for any such charges, including roaming data charges if you use the application outside of your home territory (i.e., region or country) without disabling data roaming. If you are not the bill payer for the device on which you are using the application, they assume that you have obtained permission from the bill payer.")

        CustomContent(text = "Similarly, the Service Provider cannot always assume responsibility for your usage of the application. For instance, it is your responsibility to ensure that your device remains charged. If your device runs out of battery and you are unable to access the Service, the Service Provider cannot be held responsible.")

        CustomContent(text = "In terms of the Service Provider's responsibility for your use of the application, it is important to note that while they strive to ensure that it is updated and accurate at all times, they do rely on third parties to provide information to them so that they can make it available to you. The Service Provider accepts no liability for any loss, direct or indirect, that you experience as a result of relying entirely on this functionality of the application.")

        CustomContent(text = "The Service Provider may wish to update the application at some point. The application is currently available as per the requirements for the operating system (and for any additional systems they decide to extend the availability of the application to) may change, and you will need to download the updates if you want to continue using the application. The Service Provider does not guarantee that it will always update the application so that it is relevant to you and/or compatible with the particular operating system version installed on your device. However, you agree to always accept updates to the application when offered to you. The Service Provider may also wish to cease providing the application and may terminate its use at any time without providing termination notice to you. Unless they inform you otherwise, upon any termination, (a) the rights and licenses granted to you in these terms will end; (b) you must cease using the application, and (if necessary) delete it from your device.")

        CustomHeader(text = "Changes to These Terms and Conditions")
        CustomContent(text = "The Service Provider may periodically update their Terms and Conditions. Therefore, you are advised to review this page regularly for any changes. The Service Provider will notify you of any changes by posting the new Terms and Conditions on this page.")

        CustomContent(text = "These terms and conditions are effective as of 2024-10-27")

        CustomHeader(text = "Contact Us")
        CustomContent(text = "If you have any questions or suggestions about the Terms and Conditions, please do not hesitate to contact the Service Provider at cbrf2002@proton.me.")

        CustomContent(text = "This Terms and Conditions page was generated by App Privacy Policy Generator")
    }
}

@Composable
fun BulletPointList(items: List<String>) {
    Column {
        items.forEach { item ->
            Text(text = "• $item", style = MaterialTheme.typography.bodyMedium)
        }
    }
}