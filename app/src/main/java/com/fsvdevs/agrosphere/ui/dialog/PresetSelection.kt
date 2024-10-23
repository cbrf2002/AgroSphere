package com.fsvdevs.agrosphere.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PresetSelection(
    onPresetSelected: (ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>, String) -> Unit,
    currentTempRange: MutableState<ClosedFloatingPointRange<Float>>,
    currentHumRange: MutableState<ClosedFloatingPointRange<Float>>,
    currentPreset: MutableState<String>
) {
    val presets = listOf(
        "Seedling Phase" to (18f..24f to 60f..70f),
        "Flowering Phase" to (20f..25f to 70f..80f),
        "Vegetative Phase" to (22f..29f to 50f..60f),
        "Manual Range" to (0f..60f to 0f..100f)
    )

    // Store previous values to revert if needed
    val previousTempRange = remember { mutableStateOf(currentTempRange.value) }
    val previousHumRange = remember { mutableStateOf(currentHumRange.value) }
    val previousPreset = remember { mutableStateOf(currentPreset.value) }

    // Automatically select preset index based on current preset value
    val selectedPresetIndex = remember { mutableIntStateOf(presets.indexOfFirst { it.first == currentPreset.value }) }

    LaunchedEffect(currentTempRange.value, currentHumRange.value, currentPreset.value) {
        selectedPresetIndex.intValue = presets.indexOfFirst { it.first == currentPreset.value }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = "Presets",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                itemsIndexed(presets) { index, (name, range) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).padding(bottom = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (name != "Manual Range") {
                                Text(
                                    text = "${range.first.start}-${range.first.endInclusive}°C, ${range.second.start}-${range.second.endInclusive}%",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        RadioButton(
                            selected = selectedPresetIndex.intValue == index,
                            onClick = {
                                selectedPresetIndex.intValue = index
                                previousTempRange.value = currentTempRange.value // Store previous temp range
                                previousHumRange.value = currentHumRange.value // Store previous humidity range
                                previousPreset.value = currentPreset.value // Store previous preset name

                                if (name == "Manual Range") {
                                    // Use previous values if they exist, otherwise use default
                                    currentTempRange.value = previousTempRange.value
                                    currentHumRange.value = previousHumRange.value
                                } else {
                                    val (tempRange, humRange) = range
                                    currentTempRange.value = tempRange
                                    currentHumRange.value = humRange
                                }

                                currentPreset.value = name // Update current preset state
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onPresetSelected(currentTempRange.value, currentHumRange.value, currentPreset.value)
                    },
                    modifier = Modifier.wrapContentWidth().padding(end = 8.dp)
                ) {
                    Text(text = "Save")
                }
                OutlinedButton(
                    onClick = {
                        // Revert to previous values
                        currentTempRange.value = previousTempRange.value
                        currentHumRange.value = previousHumRange.value
                        currentPreset.value = previousPreset.value
                        // Ensure to reset selected index based on previous preset
                        selectedPresetIndex.intValue = presets.indexOfFirst { it.first == previousPreset.value }
                    },
                    modifier = Modifier.wrapContentWidth().padding(end = 8.dp)
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}