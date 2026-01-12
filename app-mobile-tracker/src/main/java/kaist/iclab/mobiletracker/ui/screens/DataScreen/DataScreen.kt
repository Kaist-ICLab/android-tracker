package kaist.iclab.mobiletracker.ui.screens.DataScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.repository.SensorInfo
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.data.DataViewModel
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Data screen - displays a list of all sensors with their record counts.
 */
@Composable
fun DataScreen(
    navController: NavController,
    viewModel: DataViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = Styles.SCREEN_HORIZONTAL_PADDING)
    ) {
        Spacer(modifier = Modifier.height(Styles.TOP_SPACER_HEIGHT))

        // Header with title and refresh button
        // Header with title and refresh button
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.data_screen_title),
                fontSize = Styles.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
            Text(
                text = stringResource(R.string.data_screen_description),
                fontSize = Styles.DESCRIPTION_FONT_SIZE,
                color = AppColors.TextSecondary,
                modifier = Modifier.padding(top = Styles.SUBTITLE_TOP_PADDING)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.data_screen_subtitle, uiState.totalRecords),
                    fontSize = Styles.SUBTITLE_FONT_SIZE,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.data_screen_refresh),
                        tint = AppColors.TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Styles.SECTION_SPACING))

        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.PrimaryColor)
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            color = AppColors.TextSecondary
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Styles.ITEM_SPACING),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.sensors) { sensor ->
                            SensorListItem(
                                sensor = sensor,
                                onClick = { 
                                    navController.navigate(Screen.SensorDetail.createRoute(sensor.sensorId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorListItem(
    sensor: SensorInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Styles.CARD_SHAPE)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Styles.CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Styles.CARD_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with colored background
            Box(
                modifier = Modifier
                    .size(Styles.ICON_CONTAINER_SIZE)
                    .clip(RoundedCornerShape(Styles.ICON_CORNER_RADIUS))
                    .background(getSensorColor(sensor.sensorId).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getSensorIcon(sensor.sensorId),
                    contentDescription = sensor.displayName,
                    tint = getSensorColor(sensor.sensorId),
                    modifier = Modifier.size(Styles.ICON_SIZE)
                )
            }

            Spacer(modifier = Modifier.width(Styles.ICON_TEXT_SPACING))

            // Sensor info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sensor.displayName,
                        fontSize = Styles.SENSOR_NAME_FONT_SIZE,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (sensor.isWatchSensor) {
                        Spacer(modifier = Modifier.width(Styles.BADGE_SPACING))
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = "Watch sensor",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(Styles.BADGE_SIZE)
                        )
                    }
                }
                Text(
                    text = formatLastRecorded(sensor.lastRecordedTime),
                    fontSize = Styles.LAST_RECORDED_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }

            // Record count
            Text(
                text = formatRecordCount(sensor.recordCount),
                fontSize = Styles.RECORD_COUNT_FONT_SIZE,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.width(Styles.CHEVRON_SPACING))

            // Chevron
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = AppColors.TextSecondary,
                modifier = Modifier.size(Styles.CHEVRON_SIZE)
            )
        }
    }
}

/**
 * Format the last recorded time as a relative string.
 */
private fun formatLastRecorded(timestamp: Long?): String {
    if (timestamp == null) return "No data"
    
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "$minutes min ago"
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "$hours hour${if (hours > 1) "s" else ""} ago"
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "$days day${if (days > 1) "s" else ""} ago"
        }
        else -> {
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
}

/**
 * Format record count with K/M suffix for large numbers.
 */
private fun formatRecordCount(count: Int): String {
    return count.toString()
}

/**
 * Get the appropriate icon for a sensor.
 */
private fun getSensorIcon(sensorId: String): ImageVector {
    return when (sensorId) {
        "AmbientLight" -> Icons.Default.LightMode
        "AppListChange" -> Icons.Default.AppRegistration
        "AppUsage" -> Icons.Default.GridView
        "Battery" -> Icons.Default.BatteryChargingFull
        "BluetoothScan" -> Icons.Default.Bluetooth
        "CallLog" -> Icons.Default.Call
        "Connectivity" -> Icons.Default.Wifi
        "DataTraffic" -> Icons.Default.DataUsage
        "DeviceMode" -> Icons.Default.SettingsSuggest
        "Location" -> Icons.Default.Place
        "Media" -> Icons.Default.PlayCircleOutline
        "MessageLog" -> Icons.AutoMirrored.Filled.Message
        "Notification" -> Icons.Default.Notifications
        "Screen" -> Icons.Default.StayCurrentPortrait
        "Step" -> Icons.AutoMirrored.Filled.DirectionsWalk
        "UserInteraction" -> Icons.Default.TouchApp
        "WifiScan" -> Icons.Default.WifiTethering
        "WatchAccelerometer" -> Icons.Default.Speed
        "WatchEDA" -> Icons.Default.Waves
        "WatchHeartRate" -> Icons.Default.FavoriteBorder
        "WatchPPG" -> Icons.Default.MonitorHeart
        "WatchSkinTemperature" -> Icons.Default.Thermostat
        else -> Icons.Default.DataUsage
    }
}

/**
 * Get the appropriate color for a sensor.
 */
private fun getSensorColor(sensorId: String): Color {
    return when (sensorId) {
        "AmbientLight" -> Color(0xFFFF9800)
        "AppListChange" -> Color(0xFFE91E63)
        "AppUsage" -> Color(0xFF9C27B0)
        "Battery" -> Color(0xFFFBBC04)
        "BluetoothScan" -> Color(0xFF3F51B5)
        "CallLog" -> Color(0xFF8BC34A)
        "Connectivity" -> Color(0xFF00ACC1)
        "DataTraffic" -> Color(0xFF009688)
        "DeviceMode" -> Color(0xFF795548)
        "Location" -> Color(0xFF4285F4)
        "Media" -> Color(0xFFFF5722)
        "MessageLog" -> Color(0xFFCDDC39)
        "Notification" -> Color(0xFFEA4335)
        "Screen" -> Color(0xFF607D8B)
        "Step" -> Color(0xFF34A853)
        "UserInteraction" -> Color(0xFF673AB7)
        "WifiScan" -> Color(0xFF00BCD4)
        "WatchAccelerometer" -> Color(0xFF3F51B5)
        "WatchEDA" -> Color(0xFF00BCD4)
        "WatchHeartRate" -> Color(0xFFE91E63)
        "WatchPPG" -> Color(0xFFFF5722)
        "WatchSkinTemperature" -> Color(0xFFFF9800)
        else -> Color(0xFF9E9E9E)
    }
}
