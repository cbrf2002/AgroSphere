package com.fsvdevs.agrosphere.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import com.fsvdevs.agrosphere.viewmodel.SensorDataViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color
import android.widget.TextView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(sensorDataViewModel: SensorDataViewModel) {
    val sensorHistory = sensorDataViewModel.sensorHistory.collectAsState()
    var selectedTabIndex = remember { mutableIntStateOf(0) }
    val timeRanges = listOf("Hour", "Day", "Week", "Month", "Year")
    var isThereData = remember { mutableStateOf(false) }

    // Fetch sensor data when the tab index changes
    LaunchedEffect(selectedTabIndex.intValue) {
        sensorDataViewModel.fetchSensorHistoryByRange(timeRanges[selectedTabIndex.intValue])
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 0.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = if (isThereData.value) RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp) else RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sensor Data Monitoring",
                style = MaterialTheme.typography.titleLarge.copy(),
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex.intValue,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                timeRanges.forEachIndexed { index, range ->
                    Tab(
                        selected = selectedTabIndex.intValue == index,
                        onClick = { selectedTabIndex.intValue = index },
                        text = { Text(range) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .animateContentSize()
            ) {
                if (sensorHistory.value.isNotEmpty()) {
                    isThereData.value = true
                    val sensorProperties = listOf(
                        Pair("Temperature") { sensorData: SensorData -> sensorData.temperature },
                        Pair("Humidity") { sensorData: SensorData -> sensorData.humidity },
                        Pair("CO2 Level") { sensorData: SensorData -> sensorData.carbonDioxide },
                        Pair("Water Level") { sensorData: SensorData -> sensorData.waterLevel },
                        Pair("Water Temperature") { sensorData: SensorData -> sensorData.waterTemp },
                        Pair("Light Level") { sensorData: SensorData -> sensorData.lightLevel },
                        Pair("pH Level") { sensorData: SensorData -> sensorData.pH },
                        Pair("TDS Level") { sensorData: SensorData -> sensorData.tds }
                    )

                    sensorProperties.forEach { (title, valueMapper) ->
                        SensorChart(
                            title = title,
                            sensorDataList = sensorHistory.value,
                            valueMapper = valueMapper,
                            timeRange = timeRanges[selectedTabIndex.intValue]
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    isThereData.value = false
                    Text(
                        text = "No data available for the selected range.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
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
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    val firstTimestamp = sensorDataList.firstOrNull()?.timestamp ?: 0L
    val entries = sensorDataList.map { data ->
        Entry((data.timestamp - firstTimestamp).toFloat() / 1000, valueMapper(data).toFloat())
    }

    val lineDataSet = LineDataSet(entries, title).apply {
        color = MaterialTheme.colorScheme.primary.toArgb()
        lineWidth = 2f
        setDrawCircles(false)
        valueTextColor = Color.TRANSPARENT
        valueTextSize = 10f
        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        isHighlightEnabled = true
        setDrawHighlightIndicators(true)
    }

    val lineData = LineData(lineDataSet)
    val context = LocalContext.current
    val textColorOnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val textColorOutline = MaterialTheme.colorScheme.outline.toArgb()

    // Re-create the chart when timeRange changes or sensorDataList changes
    val lineChart = remember(sensorDataList, timeRange) {
        LineChart(context).apply {
            data = lineData
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return formatTimestamp(value, timeRange, firstTimestamp)
                    }
                }
                granularity = when (timeRange) { // Set granularity based on time range
                    "Hour" -> 600f
                    "Day" -> 3600f
                    "Week" -> 86400f
                    "Month" -> 86400f
                    "Year" -> 2592000f
                    else -> 600f
                }
                setLabelCount(5, true)
                setAvoidFirstLastClipping(true)

                textColor = textColorOnSurface
                axisLineColor = textColorOnSurface
                gridColor = textColorOutline
            }
            axisLeft.apply {
                textColor = textColorOnSurface
                axisLineColor = textColorOnSurface
                gridColor = textColorOutline
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)

            val markerView = CustomMarkerView(context) // Create a custom marker view
            marker = markerView
        }
    }

    Box(modifier = Modifier.height(200.dp)) {
        AndroidView(
            factory = { lineChart },
            modifier = Modifier.fillMaxSize(),
            update = { chart ->
                // Update the chart with new data and invalidate it
                chart.data = lineData
                chart.xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return formatTimestamp(value, timeRange, firstTimestamp)
                    }
                }
                chart.notifyDataSetChanged() // Update the chart
                chart.invalidate() // Redraw the chart
            }
        )
    }
}

// Format timestamp for the X-axis
private fun formatTimestamp(timestamp: Float, timeRange: String, firstTimestamp: Long): String {
    val sdf = when (timeRange) { // Create a SimpleDateFormat based on the time range
        "Hour" -> SimpleDateFormat("HH:mm", Locale.getDefault())
        "Day" -> SimpleDateFormat("MMM dd HH:mm", Locale.getDefault())
        "Week" -> SimpleDateFormat("MMM dd", Locale.getDefault())
        "Month" -> SimpleDateFormat("MMM dd", Locale.getDefault())
        "Year" -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
        else -> SimpleDateFormat("MMM dd", Locale.getDefault())
    }
    return sdf.format(Date((firstTimestamp + timestamp.toLong() * 1000))) // Convert to milliseconds
}

// Custom marker view for point interactions
class CustomMarkerView(context: Context) : com.github.mikephil.charting.components.MarkerView(context, R.layout.custom_marker_view) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: com.github.mikephil.charting.highlight.Highlight?) {
        tvContent.text = String.format(Locale.getDefault(), "%.2f", e?.y ?: 0f)  // Set the value to display
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        // Center the marker horizontally above the selected point
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}