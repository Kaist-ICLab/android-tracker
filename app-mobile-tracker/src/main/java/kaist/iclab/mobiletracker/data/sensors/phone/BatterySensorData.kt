package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing battery sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property timestamp Unix timestamp in milliseconds when the battery data was recorded.
 * @property level Battery level as a percentage (0 to 100).
 * @property connectedType Charging connection type (e.g., BatteryManager.BATTERY_PLUGGED_AC, BATTERY_PLUGGED_USB, BATTERY_PLUGGED_WIRELESS).
 * @property status Battery status (e.g., BatteryManager.BATTERY_STATUS_CHARGING, BATTERY_STATUS_DISCHARGING, BATTERY_STATUS_FULL, BATTERY_STATUS_NOT_CHARGING).
 * @property temperature Battery temperature in degrees Celsius.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class BatterySensorData(
    val uuid: String? = null,
    @SerialName("device_type")
    val deviceType: String,
    val timestamp: Long,
    val level: Int,
    @SerialName("connected_type")
    val connectedType: Int,
    val status: Int,
    val temperature: Int,
    val received: Long
)

