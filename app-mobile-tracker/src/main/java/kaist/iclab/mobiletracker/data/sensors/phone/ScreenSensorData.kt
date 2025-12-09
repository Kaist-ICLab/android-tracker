package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing screen sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property timestamp Timestamp in "YYYY-MM-DD HH:mm:ss" format when the screen event was recorded.
 * @property type Type of screen event (e.g., "android.intent.action.SCREEN_ON", "android.intent.action.SCREEN_OFF", "android.intent.action.USER_PRESENT").
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class ScreenSensorData(
    val uuid: String? = null,
    val timestamp: String,
    val type: String,
    val received: Long
)

