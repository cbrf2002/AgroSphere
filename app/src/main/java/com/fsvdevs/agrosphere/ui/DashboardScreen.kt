package com.fsvdevs.agrosphere.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
import androidx.compose.ui.window.Dialog
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.models.ActuatorData
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.models.SensorRangeData
import com.fsvdevs.agrosphere.ui.dialog.PresetSelection
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

    val isLoadingSensor = sensorData == null
    val isLoadingActuator = actuatorData == null

    var tempRange = remember { mutableStateOf(0f..60f) }
    var humRange = remember { mutableStateOf(0f..100f) }
    var preset = remember { mutableStateOf("") }

    LaunchedEffect(sensorData, actuatorData, sensorRangeData) {
        sensorRangeData?.let {
            // Update ranges and preset based on fetched data
            if (it.preset == "Manual Range") {
                tempRange.value = it.tempRangeLow.toFloat()..it.tempRangeHigh.toFloat()
                humRange.value = it.humRangeLow.toFloat()..it.humRangeHigh.toFloat()
                preset.value = "Manual Range"
            } else {
                // For other presets, use their specific ranges
                tempRange.value = it.tempRangeLow.toFloat()..it.tempRangeHigh.toFloat()
                humRange.value = it.humRangeLow.toFloat()..it.humRangeHigh.toFloat()
                preset.value = it.preset
            }
        } ?: run {
            // This runs if sensorRangeData is null, indicating that the app is starting fresh.
            preset.value = "Manual Range"
        }

        Log.d("DashboardScreen", "Is loading sensor: $isLoadingSensor, Is loading actuator: $isLoadingActuator")
        Log.d("DashboardScreen", "SensorData: $sensorData, ActuatorData: $actuatorData")
        Log.d("DashboardScreen", "SensorRangeData: $sensorRangeData")
        Log.d("DashboardScreen", "TempRange: $tempRange, HumRange: $humRange")
        Log.d("DashboardScreen", "Preset: $preset")
    }

    val sdf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    val currentDate = sdf.format(Date())

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
            sensorData?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SensorRow( //Temperature
                            iconResId = R.drawable.rounded_device_thermostat_24,
                            contentDescription = "Temperature",
                            value = "${data.temperature}°C",
                            label = "Temperature"
                        )
                        SensorRow( //Humidity
                            iconResId = R.drawable.rounded_humidity_percentage_24,
                            contentDescription = "Humidity",
                            value = "${data.humidity}%",
                            label = "Humidity"
                        )
                        SensorRow( //CO2 Level
                            iconResId = R.drawable.round_co2_24,
                            contentDescription = "CO2 Level",
                            value = "${data.carbonDioxide} ppm",
                            label = "CO2 Level"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SensorRow( //Water Temp
                            iconResId = R.drawable.rounded_dew_point_24,
                            contentDescription = "Water Temp",
                            value = "${data.waterTemp}°C",
                            label = "Water Temp"
                        )
                        SensorRow( //Water Level
                            iconResId = R.drawable.rounded_water_24,
                            contentDescription = "Water Level",
                            value = "${data.waterLevel} cm",
                            label = "Water Level"
                        )
                        SensorRow( //Light Level
                            iconResId = R.drawable.rounded_light_mode_24,
                            contentDescription = "Light Level",
                            value = "${data.lightLevel} lux",
                            label = "Light Level"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SensorRow( //pH Level
                            iconResId = R.drawable.rounded_water_ph_24,
                            contentDescription = "pH Level",
                            value = "${data.pH}",
                            label = "pH Level"
                        )
                        SensorRow( //TDS Level
                            iconResId = R.drawable.rounded_total_dissolved_solids_24,
                            contentDescription = "TDS Level",
                            value = "${data.tds} ppm",
                            label = "TDS Level"
                        )
                    }

                    Text(
                        text = "Last Updated: ${formatTimestamp(data.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
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
                // Automatic Climate Control Mode Switch
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
                        checked = actuatorData?.auto == true, // Bind to isAuto value from actuatorData
                        onCheckedChange = { isChecked ->
                            actuatorData?.let { data ->
                                coroutineScope.launch {
                                    // Update isAuto in the Realtime Database
                                    actuatorDataViewModel.updateActuatorData(data.copy(auto = isChecked))
                                }
                            }
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (data.fan) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    contentColor = if (data.fan) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f),
                                    disabledContainerColor = if (data.fan) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    disabledContentColor = if (data.fan) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
                                ),
                                enabled = !data.auto, // Disable button if in auto mode
                                onClick = {
                                    actuatorData.let { data ->
                                        coroutineScope.launch {
                                            actuatorDataViewModel.updateActuatorData(data.copy(fan = !data.fan))
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = if (data.fan) "Fan On" else "Fan Off",
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (data.mist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    contentColor = if (data.mist) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f),
                                    disabledContainerColor = if (data.mist) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    disabledContentColor = if (data.mist) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
                                ),
                                enabled = !data.auto, // Disable button if in auto mode
                                onClick = {
                                    actuatorData.let { data ->
                                        coroutineScope.launch {
                                            actuatorDataViewModel.updateActuatorData(data.copy(mist = !data.mist))
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = if (data.mist) "Mist On" else "Mist Off",
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (data.vent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                contentColor = if (data.vent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f),
                                disabledContainerColor = if (data.vent) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                disabledContentColor = if (data.vent) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
                            ),
                            enabled = !data.auto, // Disable button if in auto mode
                            onClick = {
                                actuatorData.let { data ->
                                    coroutineScope.launch {
                                        actuatorDataViewModel.updateActuatorData(data.copy(vent = !data.vent))
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = if (data.vent) "Vent Opened" else "Vent Closed",
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
                    PresetButton(
                        sensorRangeDataViewModel = sensorRangeDataViewModel,
                        onPresetSelected = { presetTempRange, presetHumRange, selectedPresetName ->
                            tempRange.value = presetTempRange
                            humRange.value = presetHumRange
                            preset.value = selectedPresetName
                        },
                        currentTempRange = tempRange,
                        currentHumRange = humRange,
                        currentPresetName = preset
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Temperature: ${tempRange.value.start.roundToOneDecimal()}°C - ${tempRange.value.endInclusive.roundToOneDecimal()}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RangeSlider(
                        value = tempRange.value,
                        onValueChange = { range ->
                            tempRange.value = range.start.roundToOneDecimal()..range.endInclusive.roundToOneDecimal()
                        },
                        onValueChangeFinished = {
                            val newPreset = if (preset.value.isEmpty()) "Manual Range" else preset.value
                            sensorRangeDataViewModel.updateSensorRangeData(
                                SensorRangeData(
                                    tempRangeHigh = tempRange.value.endInclusive.toDouble(),
                                    tempRangeLow = tempRange.value.start.toDouble(),
                                    humRangeHigh = humRange.value.endInclusive.toDouble(),
                                    humRangeLow = humRange.value.start.toDouble(),
                                    preset = newPreset // Use the new preset
                                )
                            )
                        },
                        valueRange = 0f..60f,
                        enabled = preset.value.contains("Manual Range")
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Humidity: ${humRange.value.start.roundToOneDecimal()}% - ${humRange.value.endInclusive.roundToOneDecimal()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RangeSlider(
                        value = humRange.value,
                        onValueChange = { range ->
                            humRange.value = range.start.roundToOneDecimal()..range.endInclusive.roundToOneDecimal()
                        },
                        onValueChangeFinished = {
                            val newPreset = if (preset.value.isEmpty()) "Manual Range" else preset.value
                            sensorRangeDataViewModel.updateSensorRangeData(
                                SensorRangeData(
                                    tempRangeHigh = tempRange.value.endInclusive.toDouble(),
                                    tempRangeLow = tempRange.value.start.toDouble(),
                                    humRangeHigh = humRange.value.endInclusive.toDouble(),
                                    humRangeLow = humRange.value.start.toDouble(),
                                    preset = newPreset // Use the new preset
                                )
                            )
                        },
                        valueRange = 0f..100f,
                        enabled = preset.value.contains("Manual Range")
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SensorRow(
    iconResId: Int,
    contentDescription: String,
    value: String,
    label: String
) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconResId),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.W700
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PresetButton(
    sensorRangeDataViewModel: SensorRangeDataViewModel,
    onPresetSelected: (ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>, String) -> Unit,
    currentTempRange: MutableState<ClosedFloatingPointRange<Float>>,
    currentHumRange: MutableState<ClosedFloatingPointRange<Float>>,
    currentPresetName: MutableState<String>
) {
    val openDialog = remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { openDialog.value = true }
    ) {
        Text(text = currentPresetName.value)
    }

    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            PresetSelection(
                onPresetSelected = { tempRange, humRange, presetName ->
                    currentPresetName.value = presetName // Update preset name
                    updateSensorRangeInDatabase(sensorRangeDataViewModel, tempRange, humRange, presetName) // Pass preset name here
                    openDialog.value = false
                },
                currentTempRange = currentTempRange,
                currentHumRange = currentHumRange,
                currentPreset = currentPresetName
            )
        }
    }
}

fun updateSensorRangeInDatabase(
    sensorRangeDataViewModel: SensorRangeDataViewModel,
    tempRange: ClosedFloatingPointRange<Float>,
    humRange: ClosedFloatingPointRange<Float>,
    preset: String // Add preset parameter
) {
    // Create a SensorRangeData object with the updated values
    val updatedData = SensorRangeData(
        tempRangeLow = tempRange.start.toDouble(),
        tempRangeHigh = tempRange.endInclusive.toDouble(),
        humRangeLow = humRange.start.toDouble(),
        humRangeHigh = humRange.endInclusive.toDouble(),
        preset = preset
    )

    // Update the database
    sensorRangeDataViewModel.updateSensorRangeData(updatedData) // Ensure this function is implemented correctly
}

// Extension function to round Float to the nearest 0.1
fun Float.roundToOneDecimal(): Float {
    return (this * 10).roundToInt() / 10f
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(date)
}