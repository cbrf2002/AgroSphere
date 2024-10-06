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
    val sensorHistory = sensorDataViewModel.sensorHistory.collectAsState() // Observing sensor history
    var selectedTabIndex = remember { mutableIntStateOf(0) }
    val timeRanges = listOf("Hour", "Day", "Week", "Month", "Year")
    var isThereData = remember{ mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(16.dp, 16.dp, 16.dp, 0.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = if(isThereData.value) RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp) else RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sensor Data Monitoring",
                style = MaterialTheme.typography.titleLarge.copy(),
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            PrimaryTabRow(
                selectedTabIndex.intValue,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ) {
                timeRanges.forEachIndexed { index, range ->
                    Tab(
                        selected = selectedTabIndex.intValue == index,
                        onClick = {
                            selectedTabIndex.intValue = index
                            sensorDataViewModel.fetchSensorHistoryByRange(range)
                        },
                        text = { Text(range) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                if (sensorHistory.value.isNotEmpty()) {
                    isThereData.value = true
                    val sensorProperties = listOf(
                        Pair("Temperature") { sensorData: SensorData -> sensorData.temperature },
                        Pair("Humidity") { sensorData: SensorData -> sensorData.humidity },
                        Pair("pH Level") { sensorData: SensorData -> sensorData.pH },
                        Pair("Light Level") { sensorData: SensorData -> sensorData.lightLevel },
                        Pair("Water Temperature") { sensorData: SensorData -> sensorData.waterTemp },
                        Pair("Water Level") { sensorData: SensorData -> sensorData.waterLevel }
                    )

                    // Render charts for each sensor type
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
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    val entries = sensorDataList.map { data ->
        Entry(data.timestamp.toFloat(), valueMapper(data).toFloat())
    }

    val lineDataSet = LineDataSet(entries, title).apply {
        color = MaterialTheme.colorScheme.primary.toArgb()
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(MaterialTheme.colorScheme.primary.toArgb())
        valueTextColor = Color.TRANSPARENT  // Hide labels
        valueTextSize = 10f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        isHighlightEnabled = true
        setDrawHighlightIndicators(true)
    }

    val lineData = LineData(lineDataSet)

    val context = LocalContext.current
    val lineChart = remember { LineChart(context) }

    // Custom marker view to show label when user taps a point
    val marker = CustomMarkerView(context)
    lineChart.marker = marker

    // Configure chart attributes
    lineChart.apply {
        data = lineData
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return formatTimestamp(value, timeRange)
                }
            }
            granularity = when (timeRange) {
                "Hour" -> 600f   // 10 minutes
                "Day" -> 3600f   // 1 hour
                "Week" -> 86400f // 1 day
                "Month" -> 86400f // 1 day
                "Year" -> 2592000f // 1 month
                else -> 600f
            }
            setLabelCount(5, true)  // Set number of labels
            xAxis.setAvoidFirstLastClipping(true)
        }
        axisLeft.apply {
            // Customize Y-axis if needed
        }
        axisRight.isEnabled = false
        legend.isEnabled = false
        description.isEnabled = false
        setTouchEnabled(true)
        setDragEnabled(true)
        setScaleEnabled(true)
        setPinchZoom(true)

        // Update chart when data changes
        notifyDataSetChanged()
        invalidate()
    }

    // Display the chart in a Box
    Box(modifier = Modifier.height(200.dp)) {
        AndroidView(
            factory = { lineChart },
            modifier = Modifier.fillMaxSize(),
            update = { it.invalidate() }  // Refresh chart
        )
    }
}

// Format timestamp for the X-axis
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

// Custom marker view for point interactions
class CustomMarkerView(context: Context) : com.github.mikephil.charting.components.MarkerView(context, R.layout.custom_marker_view) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: com.github.mikephil.charting.highlight.Highlight?) {
        tvContent.text = "${e?.y?.toString()}"  // Set the value to display
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        // Center the marker horizontally above the selected point
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}