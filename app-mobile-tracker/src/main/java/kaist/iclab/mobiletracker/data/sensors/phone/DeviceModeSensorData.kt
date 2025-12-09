package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing device mode sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 * @property timestamp Timestamp in "YYYY-MM-DD HH:mm:ss" format when the device mode event was recorded.
 * @property eventType Type of device mode event (e.g., "NOTIFICATION_MODE_EVENT", "POWER_SAVE_MODE_EVENT", "AIRPLANE_MODE_EVENT").
 * @property value Value of the device mode event (e.g., "NOTIFICATION_MODE_FILTER_ALL", "POWER_SAVE_MODE_ON", "AIRPLANE_MODE_OFF").
 */
@Serializable
data class DeviceModeSensorData(
    val uuid: String? = null,
    val received: Long,
    val timestamp: String,
    val eventType: String,
    val value: String
)

