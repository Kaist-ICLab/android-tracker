package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import kaist.iclab.mobiletracker.R

/**
 * Data class representing sensor configuration
 */
private data class SensorConfig(
    val patterns: List<String>,
    val stringRes: Int,
    val icon: ImageVector
)

/**
 * Sensor configurations mapping patterns to string resources and icons
 */
private val sensorConfigs = listOf(
    SensorConfig(
        listOf("Ambient Light"),
        R.string.sensor_desc_ambient_light,
        Icons.Filled.BrightnessMedium
    ),
    SensorConfig(
        listOf("App List Change"),
        R.string.sensor_desc_app_list_change,
        Icons.Filled.List
    ),
    SensorConfig(listOf("App Usage"), R.string.sensor_desc_app_usage, Icons.Filled.Apps),
    SensorConfig(listOf("Battery"), R.string.sensor_desc_battery, Icons.Filled.BatteryFull),
    SensorConfig(listOf("Bluetooth"), R.string.sensor_desc_bluetooth, Icons.Filled.Bluetooth),
    SensorConfig(listOf("Call Log"), R.string.sensor_desc_call_log, Icons.Filled.History),
    SensorConfig(listOf("Data Traffic"), R.string.sensor_desc_data_traffic, Icons.Filled.DataUsage),
    SensorConfig(
        listOf("Device Mode"),
        R.string.sensor_desc_device_mode,
        Icons.Filled.PhoneAndroid
    ),
    SensorConfig(listOf("Location"), R.string.sensor_desc_location, Icons.Filled.LocationOn),
    SensorConfig(listOf("Media"), R.string.sensor_desc_media, Icons.Filled.PlayArrow),
    SensorConfig(
        listOf("Message"),
        R.string.sensor_desc_message,
        Icons.AutoMirrored.Filled.Message
    ),
    SensorConfig(
        listOf("Network Change"),
        R.string.sensor_desc_network_change,
        Icons.Filled.NetworkCheck
    ),
    SensorConfig(
        listOf("Notification"),
        R.string.sensor_desc_notification,
        Icons.Filled.Notifications
    ),
    SensorConfig(listOf("Screen"), R.string.sensor_desc_screen, Icons.Filled.Phone),
    SensorConfig(
        listOf("Step"),
        R.string.sensor_desc_step,
        Icons.AutoMirrored.Filled.DirectionsWalk
    ),
    SensorConfig(
        listOf("User Interaction"),
        R.string.sensor_desc_user_interaction,
        Icons.Filled.TouchApp
    ),
    SensorConfig(listOf("Wifi", "WiFi"), R.string.sensor_desc_wifi, Icons.Filled.Wifi)
)

private val defaultConfig = SensorConfig(
    patterns = emptyList(),
    stringRes = R.string.sensor_desc_default,
    icon = Icons.Filled.Settings
)

/**
 * Finds the matching sensor configuration for a given sensor name
 */
private fun findSensorConfig(sensorName: String): SensorConfig {
    return sensorConfigs.firstOrNull { config ->
        config.patterns.any { pattern ->
            sensorName.contains(pattern, ignoreCase = true)
        }
    } ?: defaultConfig
}

/**
 * Maps sensor names to their localized descriptions (2-4 words)
 */
@Composable
fun getSensorDescription(sensorName: String): String {
    val context = LocalContext.current
    val config = findSensorConfig(sensorName)
    return context.getString(config.stringRes)
}

/**
 * Maps sensor names to their corresponding Material Icons
 */
fun getSensorIcon(sensorName: String): ImageVector {
    return findSensorConfig(sensorName).icon
}

