package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing screen sensor data from the phone device.
 *
 * @property uuid User UUID
 * @property timestamp Unix timestamp in milliseconds when the screen event was recorded.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 * @property deviceType Type of device (0 = phone, 1 = watch).
 * @property type Type of screen event (e.g., "SCREEN_ON", "SCREEN_OFF", "USER_PRESENT").
 */
@Serializable
data class ScreenSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val received: Long,
    val type: String,
    @SerialName("device_type")
    val deviceType: Int
)

