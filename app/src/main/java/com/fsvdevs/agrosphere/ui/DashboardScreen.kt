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
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.fsvdevs.agrosphere.routes.Routes
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.let

@Composable
fun DashboardScreen(
    navController: NavController,
    sensorDataViewModel: SensorDataViewModel,
    actuatorDataViewModel: ActuatorDataViewModel
) {
    val sensorData by sensorDataViewModel.sensorData.collectAsState()
    val actuatorData by actuatorDataViewModel.actuatorData.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isLoadingSensor = sensorData == null
    val isLoadingActuator = actuatorData == null

    // Logging data states
    LaunchedEffect(sensorData, actuatorData) {
        Log.d("DashboardScreen", "Is loading sensor: $isLoadingSensor, Is loading actuator: $isLoadingActuator")
        Log.d("DashboardScreen", "SensorData: $sensorData, ActuatorData: $actuatorData")
    }

    if (isLoadingSensor || isLoadingActuator) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator()
            Log.d("DashboardScreen", "Showing progress while loading data")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            sensorData?.let { data ->
                Log.d("DashboardScreen", "Displaying sensor data: $data")
                Text(text = "Temperature: ${data.temperature}°C")
                Text(text = "Humidity: ${data.humidity}%")
                Text(text = "Light Level: ${data.lightLevel} lux")
                Text(text = "Water Level: ${data.waterLevel} cm")
                Text(text = "Water Temperature: ${data.waterTemp}°C")
                Text(text = "pH Level: ${data.pH}")
                Text(text = "Last Updated: ${formatTimestamp(data.timestamp)}")
            }

            actuatorData?.let { data ->
                Log.d("DashboardScreen", "Displaying actuator data: $data")
                Text(text = "Fan Status: ${if (data.fan) "ON" else "OFF"}")
                Text(text = "Mist Status: ${if (data.mist) "ON" else "OFF"}")
                Text(text = "Vent Status: ${if (data.vent) "ON" else "OFF"}")

                Button(onClick = {
                    coroutineScope.launch {
                        actuatorDataViewModel.updateActuatorData(data.copy(fan = !data.fan))
                    }
                }) {
                    Text(text = if (data.fan) "Turn Fan OFF" else "Turn Fan ON")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        actuatorDataViewModel.updateActuatorData(data.copy(mist = !data.mist))
                    }
                }) {
                    Text(text = if (data.mist) "Turn Mist OFF" else "Turn Mist ON")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        actuatorDataViewModel.updateActuatorData(data.copy(vent = !data.vent))
                    }
                }) {
                    Text(text = if (data.vent) "Close Vent" else "Open Vent")
                }
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

@Composable
fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(date)
}

@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        navController = rememberNavController(),
        sensorDataViewModel = viewModel(),
        actuatorDataViewModel = viewModel()
    )
}