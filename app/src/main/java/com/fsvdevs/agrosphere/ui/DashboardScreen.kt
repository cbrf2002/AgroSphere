package com.fsvdevs.agrosphere.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fsvdevs.agrosphere.viewmodel.ActuatorDataViewModel
import com.fsvdevs.agrosphere.viewmodel.SensorDataViewModel
import kotlinx.coroutines.launch
import android.text.format.DateFormat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.fsvdevs.agrosphere.routes.Routes
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.let

@Composable
fun DashboardScreen(
    navController: NavController,
    sensorDataViewModel: SensorDataViewModel = viewModel(),
    actuatorDataViewModel: ActuatorDataViewModel = viewModel()
) {
    val context = LocalContext.current
    val sensorData by sensorDataViewModel.sensorData.collectAsState()
    val actuatorData by actuatorDataViewModel.actuatorData.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isLoadingSensor = sensorData == null
    val isLoadingActuator = actuatorData == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (isLoadingSensor) {
            CircularProgressIndicator()
        } else {
            sensorData?.let { data ->
                Text(text = "Temperature: ${data.temperature}°C")
                Text(text = "Humidity: ${data.humidity}%")
                Text(text = "Light Level: ${data.lightLevel} lux")
                Text(text = "Water Level: ${data.waterLevel} cm")
                Text(text = "Water Temperature: ${data.waterTemperature}°C")
                Text(text = "pH Level: ${data.phLevel}")
                Text(text = "Last Updated: ${formatTimestamp(data.timestamp)}")
            }
        }

        if (isLoadingActuator) {
            CircularProgressIndicator()
        } else {
            actuatorData?.let { data ->
                // Display actuator controls
                Text(text = "Fan Status: ${if (data.fanStatus) "ON" else "OFF"}")
                Text(text = "Fog Status: ${if (data.fogStatus) "ON" else "OFF"}")
                Text(text = "Vent Status: ${if (data.ventStatus) "ON" else "OFF"}")

                var isButtonEnabled by remember { mutableStateOf(true) }

                // Fan button
                Button(onClick = {
                    if (isButtonEnabled) {
                        isButtonEnabled = false
                        coroutineScope.launch {
                            actuatorDataViewModel.createNewActuatorDataEntry(data.copy(fanStatus = !data.fanStatus))
                            Toast.makeText(context, "Fan Status Updated", Toast.LENGTH_SHORT).show()
                            isButtonEnabled = true
                        }
                    }
                }) {
                    Text(text = if (data.fanStatus) "Turn Fan OFF" else "Turn Fan ON")
                }

                // Fog button
                Button(onClick = {
                    if (isButtonEnabled) {
                        isButtonEnabled = false
                        coroutineScope.launch {
                            actuatorDataViewModel.createNewActuatorDataEntry(data.copy(fogStatus = !data.fogStatus))
                            Toast.makeText(context, "Fog Status Updated", Toast.LENGTH_SHORT).show()
                            isButtonEnabled = true
                        }
                    }
                }) {
                    Text(text = if (data.fogStatus) "Turn Fog OFF" else "Turn Fog ON")
                }

                // Vent button
                Button(onClick = {
                    if (isButtonEnabled) {
                        isButtonEnabled = false
                        coroutineScope.launch {
                            actuatorDataViewModel.createNewActuatorDataEntry(data.copy(ventStatus = !data.ventStatus))
                            Toast.makeText(context, "Vent Status Updated", Toast.LENGTH_SHORT).show()
                            isButtonEnabled = true
                        }
                    }
                }) {
                    Text(text = if (data.ventStatus) "Close Vent" else "Open Vent")
                }

                Button(onClick = {
                    Firebase.auth.signOut()
                    navController.navigate(Routes.LOGIN_SCREEN) {
                        popUpTo(0) { inclusive = true }
                    }
                    Toast.makeText(context, "Signed out successfully.", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Sign Out")
                }
            }
        }
    }
}

@Composable
fun formatTimestamp(timestamp: Timestamp): String {
    val date = Date(timestamp.seconds * 1000)
    val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMMddHHmmss")
    val formattedDate = SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    return formattedDate
}

@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        navController = rememberNavController(),
        sensorDataViewModel = SensorDataViewModel(),
        actuatorDataViewModel = ActuatorDataViewModel()
    )
}