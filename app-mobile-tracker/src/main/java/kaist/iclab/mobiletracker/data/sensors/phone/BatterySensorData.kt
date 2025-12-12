package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing battery sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property timestamp Unix timestamp in milliseconds when the battery data was recorded.
 * @property level Battery level as a percentage (0.0 to 100.0).
 * @property plugged Charging state (e.g., "AC", "USB", "WIRELESS", "UNPLUGGED").
 * @property status Battery status (e.g., "charging", "discharging", "full", "not_charging").
 * @property temperature Battery temperature in degrees Celsius.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class BatterySensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val level: Float,
    val plugged: String,
    val status: String,
    val temperature: Int,
    val received: Long
)

