package com.fsvdevs.agrosphere.ui

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.models.SensorData
import com.fsvdevs.agrosphere.viewmodel.SensorDataViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(sensorDataViewModel: SensorDataViewModel) {
    val sensorHistory = sensorDataViewModel.sensorHistory.collectAsState()
    val isLoading = sensorDataViewModel.isLoading.collectAsState()
    val errorMessage = sensorDataViewModel.errorMessage.collectAsState()
    val databaseChanged = sensorDataViewModel.databaseChangeDetected.collectAsState()
    
    var selectedTabIndex = remember { mutableIntStateOf(0) }
    val timeRanges = listOf("Hour", "Day", "Week", "Month", "Year")
    
    val hasData = remember { derivedStateOf { sensorHistory.value.isNotEmpty() } }

    // Fetch data when tab changes
    LaunchedEffect(selectedTabIndex.intValue) {
        Log.d("MonitorScreen", "Tab changed to ${timeRanges[selectedTabIndex.intValue]}")
        sensorDataViewModel.fetchSensorHistoryByRange(timeRanges[selectedTabIndex.intValue])
    }

    // Initial data fetch and refresh when database changes are detected
    LaunchedEffect(databaseChanged.value) {
        if (databaseChanged.value) {
            Log.d("MonitorScreen", "Database change detected, refreshing")
            sensorDataViewModel.fetchSensorHistoryByRange(timeRanges[selectedTabIndex.intValue], true)
        }
    }
    
    // Initial load
    LaunchedEffect(Unit) {
        Log.d("MonitorScreen", "Initial load")
        sensorDataViewModel.fetchSensorHistoryByRange(timeRanges[selectedTabIndex.intValue])
    }

    // Background refresh with reduced frequency
    LaunchedEffect(Unit) {
        while (true) {
            delay(180000) // 3 minutes
            Log.d("MonitorScreen", "Background refresh")
            sensorDataViewModel.refreshData(timeRanges[selectedTabIndex.intValue])
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                modifier = Modifier.fillMaxHeight()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Header with refresh button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sensor Data Monitoring",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        
                        IconButton(
                            onClick = {
                                sensorDataViewModel.refreshData(timeRanges[selectedTabIndex.intValue])
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Data",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Tab row
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

                    // Loading indicator
                    AnimatedVisibility(
                        visible = isLoading.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Loading data...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    // Error message
                    AnimatedVisibility(
                        visible = !isLoading.value && errorMessage.value != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage.value ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    // Content area
                    AnimatedVisibility(
                        visible = !isLoading.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                                .animateContentSize(),
                        ) {
                            if (hasData.value) {
                                val sensorProperties = listOf(
                                    Pair("Temperature") { sensorData: SensorData -> sensorData.temperature },
                                    Pair("Humidity") { sensorData: SensorData -> sensorData.humidity },
                                    Pair("CO2 Level") { sensorData: SensorData -> sensorData.carbonDioxide },
                                    Pair("Water Temperature") { sensorData: SensorData -> sensorData.waterTemp },
                                    Pair("Light Level") { sensorData: SensorData -> sensorData.lightLevel },
                                    Pair("pH Level") { sensorData: SensorData -> sensorData.pH },
                                    Pair("TDS Level") { sensorData: SensorData -> sensorData.tds }
                                )

                                // Last update timestamp
                                val lastTimestamp = sensorHistory.value.maxByOrNull { it.timestamp }?.timestamp ?: 0L
                                if (lastTimestamp > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Latest data from: ${formatFullTimestamp(lastTimestamp)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

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
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No data available for the selected time range.\nTap the refresh button to try again.",
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Format full timestamp for display
private fun formatFullTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun SensorChart(
    title: String,
    sensorDataList: List<SensorData>,
    valueMapper: (SensorData) -> Double,
    timeRange: String
) {
    val maxFontScale = 10f
    val minFontScale = 2f
    val maxUiScale = 3f
    val minUiScale = 1.5f

    val context = LocalContext.current
    val currentDensity = LocalDensity.current

    // Adjust font scale within min and max bounds
    val adjustedFontScale = when {
        currentDensity.fontScale > maxFontScale -> maxFontScale
        currentDensity.fontScale < minFontScale -> minFontScale
        else -> currentDensity.fontScale
    }

    // Adjust UI density within min and max bounds
    val adjustedDensity = when {
        currentDensity.density > maxUiScale -> maxUiScale
        currentDensity.density < minUiScale -> minUiScale
        else -> currentDensity.density
    }

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
                textSize = adjustedFontScale * 5f
            }
            axisLeft.apply {
                textColor = textColorOnSurface
                axisLineColor = textColorOnSurface
                gridColor = textColorOutline
                textSize = adjustedFontScale * 5f
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)

            val markerView = CustomMarkerView(context, adjustedDensity) // Create a custom marker view
            marker = markerView
        }
    }

    Box(modifier = Modifier.height(200.dp)) {
        AndroidView(
            factory = { lineChart },
            modifier = Modifier.fillMaxSize(),
            update = { chart ->
                chart.xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return formatTimestamp(value, timeRange, firstTimestamp)
                    }
                }
                chart.xAxis.granularity = when (timeRange) {
                    "Hour" -> 600f
                    "Day" -> 3600f
                    "Week" -> 86400f
                    "Month" -> 86400f
                    "Year" -> 2592000f
                    else -> 600f
                }
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        )
    }
}

// Format timestamp for the X-axis
private fun formatTimestamp(timestamp: Float, timeRange: String, firstTimestamp: Long): String {
    val sdf = when (timeRange) {
        "Hour" -> SimpleDateFormat("HH:mm", Locale.getDefault())
        "Day" -> SimpleDateFormat("HH:mm", Locale.getDefault())
        "Week" -> SimpleDateFormat("MMM dd", Locale.getDefault())
        "Month" -> SimpleDateFormat("MMM dd", Locale.getDefault())
        "Year" -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
        else -> SimpleDateFormat("MMM dd", Locale.getDefault())
    }
    return sdf.format(Date((firstTimestamp + timestamp.toLong() * 1000)))
}

// Custom marker view for point interactions
class CustomMarkerView(context: Context) : com.github.mikephil.charting.components.MarkerView(context, R.layout.custom_marker_view) {

    private val tvContent: TextView = findViewById(R.id.tvContent)
    private var scaledDensity: Float = context.resources.displayMetrics.density

    constructor(context: Context, scaledDensity: Float) : this(context) {
        this.scaledDensity = scaledDensity
    }

    override fun refreshContent(e: Entry?, highlight: com.github.mikephil.charting.highlight.Highlight?) {
        tvContent.text = String.format(Locale.getDefault(), "%.2f", e?.y ?: 0f)  // Set the value to display
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        // Center the marker horizontally above the selected point
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}