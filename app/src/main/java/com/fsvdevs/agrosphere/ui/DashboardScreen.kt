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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.models.ActuatorData
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.models.SensorRangeData
import com.fsvdevs.agrosphere.viewmodel.SensorRangeDataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.let
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    sensorData: SensorData?,
    actuatorData: ActuatorData?,
    sensorRangeData: SensorRangeData?,
    sensorDataViewModel: SensorDataViewModel,
    actuatorDataViewModel: ActuatorDataViewModel,
    sensorRangeDataViewModel: SensorRangeDataViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isLoadingSensor = sensorData == null
    val isLoadingActuator = actuatorData == null

    var tempRange by remember {
        mutableStateOf(sensorRangeData?.let { it.tempRangeLow.toFloat()..it.tempRangeHigh.toFloat() } ?: 0f..60f)
    }
    var humRange by remember {
        mutableStateOf(sensorRangeData?.let { it.humRangeLow.toFloat()..it.humRangeHigh.toFloat() } ?: 0f..100f)
    }

    sensorRangeData?.let { rangeData ->
        tempRange = rangeData.tempRangeLow.toFloat()..rangeData.tempRangeHigh.toFloat()
        humRange = rangeData.humRangeLow.toFloat()..rangeData.humRangeHigh.toFloat()
    }

    val sdf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    val currentDate = sdf.format(Date())

    // Logging data states
    LaunchedEffect(sensorData, actuatorData, sensorRangeData) {
        Log.d("DashboardScreen", "Is loading sensor: $isLoadingSensor, Is loading actuator: $isLoadingActuator")
        Log.d("DashboardScreen", "SensorData: $sensorData, ActuatorData: $actuatorData")
        Log.d("DashboardScreen", "SensorRangeData: $sensorRangeData")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column( //Date and dashboard text
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = currentDate,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoadingSensor || isLoadingActuator) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Log.d("DashboardScreen", "Showing progress while loading data")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                sensorData?.let { data ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        //Temperature
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rounded_device_thermostat_24),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                contentDescription = "Temperature",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "${data.temperature}°C",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W700
                                )
                                Text(
                                    text = "Temperature",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        //Humidity
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rounded_humidity_percentage_24),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                contentDescription = "Humidity",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "${data.humidity}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W700
                                )
                                Text(
                                    text = "Humidity",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        //Light Level
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rounded_light_mode_24),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                contentDescription = "Light Level",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "${data.lightLevel} lux",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W700
                                )
                                Text(
                                    text = "Light Level",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                sensorData?.let { data ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        //Water Temperature
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rounded_dew_point_24),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                contentDescription = "Water Temp",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "${data.waterTemp}°C",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W700
                                )
                                Text(
                                    text = "Water Temp",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        //Water Level
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rounded_water_24),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                contentDescription = "Water Level",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "${data.waterLevel} cm",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W700
                                )
                                Text(
                                    text = "Water Level",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        //pH Level
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.rounded_water_ph_24),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                contentDescription = "pH Level",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "${data.pH}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W700
                                )
                                Text(
                                    text = "pH Level",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text(
                        text = "Last Updated: ${formatTimestamp(data.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                //Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Automatic Climate Control Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W700
                    )
                    Switch(
                        checked = true,
                        onCheckedChange = {
                            //TODO: Implement automatic climate control mode
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Actuator Controls",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W700),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    actuatorData?.let { data ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = if (!data.fan) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) else ButtonDefaults.buttonColors(),
                                onClick = {
                                    coroutineScope.launch {
                                        actuatorDataViewModel.updateActuatorData(data.copy(fan = !data.fan))
                                    }
                                }) {
                                Text(
                                    text = if (data.fan) "Fan On" else "Fan Off",
                                    color = if (!data.fan) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f) else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = if (!data.mist) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) else ButtonDefaults.buttonColors(),
                                onClick = {
                                    coroutineScope.launch {
                                        actuatorDataViewModel.updateActuatorData(data.copy(mist = !data.mist))
                                    }
                                }) {
                                Text(
                                    text = if (data.mist) "Mist On" else "Mist Off",
                                    color = if (!data.mist) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f) else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (!data.vent) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) else ButtonDefaults.buttonColors(),
                            onClick = {
                                coroutineScope.launch {
                                    actuatorDataViewModel.updateActuatorData(data.copy(vent = !data.vent))
                                }
                            }) {
                            Text(
                                text = if (data.vent) "Vent Opened" else "Vent Closed",
                                color = if (!data.vent) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f) else MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Climate Ranges",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W700
                    )
                    OutlinedButton(
                        onClick = {
                            //TODO: Implement climate range presets
                        }
                    ) {
                        Text(text = "Presets")
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Temperature: ${tempRange.start.roundToOneDecimal()}°C - ${tempRange.endInclusive.roundToOneDecimal()}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RangeSlider(
                        value = tempRange,
                        onValueChange = { range ->
                            tempRange = range.start.roundToOneDecimal()..range.endInclusive.roundToOneDecimal()
                        },
                        onValueChangeFinished = {
                            sensorRangeDataViewModel.updateSensorRangeData(
                                SensorRangeData(
                                    tempRangeHigh = tempRange.endInclusive.toDouble(),
                                    tempRangeLow = tempRange.start.toDouble(),
                                    humRangeHigh = humRange.endInclusive.toDouble(),
                                    humRangeLow = humRange.start.toDouble()
                                )
                            )
                        },
                        valueRange = 0f..60f
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Humidity: ${humRange.start.roundToOneDecimal()}% - ${humRange.endInclusive.roundToOneDecimal()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RangeSlider(
                        value = humRange,
                        onValueChange = { range ->
                            humRange = range.start.roundToOneDecimal()..range.endInclusive.roundToOneDecimal()
                        },
                        onValueChangeFinished = {
                            sensorRangeDataViewModel.updateSensorRangeData(
                                SensorRangeData(
                                    tempRangeHigh = tempRange.endInclusive.toDouble(),
                                    tempRangeLow = tempRange.start.toDouble(),
                                    humRangeHigh = humRange.endInclusive.toDouble(),
                                    humRangeLow = humRange.start.toDouble()
                                )
                            )
                        },
                        valueRange = 0f..100f
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// Extension function to round Float to the nearest 0.1
fun Float.roundToOneDecimal(): Float {
    return (this * 10).roundToInt() / 10f
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
        sensorData = null,
        actuatorData = null,
        sensorRangeData = null,
        navController = rememberNavController(),
        sensorDataViewModel = viewModel(),
        actuatorDataViewModel = viewModel(),
        sensorRangeDataViewModel = viewModel()
    )
}