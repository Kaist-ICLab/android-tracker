package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensorConfigSettings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.BatteryChargingFull

import androidx.compose.material.icons.filled.Bluetooth

import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.GridView

import androidx.compose.material.icons.filled.LightMode

import androidx.compose.material.icons.filled.LocationOn

import androidx.compose.material.icons.filled.Notifications


import androidx.compose.material.icons.filled.Place

import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
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
        Icons.Filled.LightMode
    ),
    SensorConfig(
        listOf("App List Change"),
        R.string.sensor_desc_app_list_change,
        Icons.Filled.AppRegistration
    ),
    SensorConfig(listOf("App Usage"), R.string.sensor_desc_app_usage, Icons.Filled.GridView),
    SensorConfig(listOf("Battery"), R.string.sensor_desc_battery, Icons.Filled.BatteryChargingFull),
    SensorConfig(listOf("Bluetooth"), R.string.sensor_desc_bluetooth, Icons.Filled.Bluetooth),
    SensorConfig(listOf("Call Log"), R.string.sensor_desc_call_log, Icons.Filled.Call),
    SensorConfig(listOf("Data Traffic"), R.string.sensor_desc_data_traffic, Icons.Filled.DataUsage),
    SensorConfig(
        listOf("Device Mode"),
        R.string.sensor_desc_device_mode,
        Icons.Filled.SettingsSuggest
    ),
    SensorConfig(listOf("Location"), R.string.sensor_desc_location, Icons.Filled.Place),
    SensorConfig(listOf("Media"), R.string.sensor_desc_media, Icons.Filled.PlayCircleOutline),
    SensorConfig(
        listOf("Message"),
        R.string.sensor_desc_message,
        Icons.AutoMirrored.Filled.Message
    ),
    SensorConfig(
        listOf("Connectivity"),
        R.string.sensor_desc_connectivity,
        Icons.Filled.Wifi
    ),
    SensorConfig(
        listOf("Notification"),
        R.string.sensor_desc_notification,
        Icons.Filled.Notifications
    ),
    SensorConfig(listOf("Screen"), R.string.sensor_desc_screen, Icons.Filled.StayCurrentPortrait),
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
    SensorConfig(listOf("Wifi", "WiFi"), R.string.sensor_desc_wifi, Icons.Filled.WifiTethering)
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

