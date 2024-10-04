package com.fsvdevs.agrosphere.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.fsvdevs.agrosphere.viewmodel.SensorDataViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.fsvdevs.agrosphere.models.SensorData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

@Composable
fun MonitorScreen(viewModel: SensorDataViewModel) {
    val sensorHistory = viewModel.sensorHistory.collectAsState() // Observing the sensor history
    val selectedTimeRange = viewModel.selectedTimeRange.collectAsState() // Observing selected time range

    // Tabs for selecting time range
    var selectedTabIndex = remember { mutableIntStateOf(0) }
    val timeRanges = listOf("Hour", "Day", "Week", "Month", "Year")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(selectedTabIndex.intValue) {
            timeRanges.forEachIndexed { index, range ->
                Tab(
                    selected = selectedTabIndex.intValue == index,
                    onClick = {
                        selectedTabIndex.intValue = index
                        viewModel.fetchSensorHistoryByRange(range) // Fetch data based on selected time range
                    },
                    text = { Text(range) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (sensorHistory.value.isNotEmpty()) {
                // Define the list of sensor properties you want to chart
                val sensorProperties = listOf(
                    Pair("Temperature") { sensorData: SensorData -> sensorData.temperature },
                    Pair("Humidity") { sensorData: SensorData -> sensorData.humidity },
                    Pair("pH Level") { sensorData: SensorData -> sensorData.pH },
                    Pair("Light Level") { sensorData: SensorData -> sensorData.lightLevel },
                    Pair("Water Temperature") { sensorData: SensorData -> sensorData.waterTemp },
                    Pair("Water Level") { sensorData: SensorData -> sensorData.waterLevel }
                )

                // Create charts for each type of sensor data
                items(sensorProperties.size) { index ->
                    val (title, valueMapper) = sensorProperties[index]

                    SensorChart(
                        title = title,
                        sensorDataList = sensorHistory.value,
                        valueMapper = valueMapper,
                        timeRange = timeRanges[selectedTabIndex.intValue]
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                item {
                    Text("No data available for the selected range.")
                }
            }
        }
    }
}

@Composable
fun SensorChart(
    title: String,
    sensorDataList: List<SensorData>,
    valueMapper: (SensorData) -> Double,
    timeRange: String
) {
    val entries = sensorDataList.map { data ->
        Entry(data.timestamp.toFloat(), valueMapper(data).toFloat())
    }

    val lineDataSet = LineDataSet(entries, title).apply {
        color = MaterialTheme.colorScheme.primary.toArgb()
        valueTextColor = Color.BLACK
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(MaterialTheme.colorScheme.primary.toArgb())
        valueTextSize = 10f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }

    val lineData = LineData(lineDataSet)

    val context = LocalContext.current
    val lineChart = remember { LineChart(context) }

    // Setup chart attributes
    lineChart.apply {
        data = lineData
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return formatTimestamp(value, timeRange)
                }
            }
        }
        axisLeft.apply {
            // Customize Y-axis properties here if needed
        }
        axisRight.isEnabled = false
        legend.isEnabled = true
        description.isEnabled = false
        setTouchEnabled(true)
        setDragEnabled(true)
        setScaleEnabled(true)
        setPinchZoom(true)
    }

    // Set the content of the LineChart in a Box
    Box(modifier = Modifier.height(200.dp)) {
        AndroidView(
            factory = { lineChart },
            modifier = Modifier.fillMaxSize(),
            update = {
                it.invalidate() // Refresh chart
            }
        )
    }

    Text(text = title, style = MaterialTheme.typography.titleMedium)
}

// Function to format timestamps for the bottom axis based on the selected time range
private fun formatTimestamp(timestamp: Float, timeRange: String): String {
    val sdf = when (timeRange) {
        "Hour" -> SimpleDateFormat("HH:mm", Locale.getDefault())
        "Day" -> SimpleDateFormat("MMM dd HH:mm", Locale.getDefault())
        "Week" -> SimpleDateFormat("MMM dd", Locale.getDefault())
        "Month" -> SimpleDateFormat("MMM dd", Locale.getDefault())
        "Year" -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
        else -> SimpleDateFormat("MMM dd", Locale.getDefault())
    }
    return sdf.format(Date(timestamp.toLong()))
}