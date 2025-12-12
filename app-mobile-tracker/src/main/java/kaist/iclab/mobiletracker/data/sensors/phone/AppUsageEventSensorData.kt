package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing app usage event sensor data from the phone device.
 *
 * @property uuid Unique identifier for the app usage event sensor entry. Auto-generated when inserting into Supabase.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property timestamp Unix timestamp in milliseconds when the app usage event was recorded.
 * @property isSystemApp Whether the app is a system app.
 * @property isUpdatedSystemApp Whether the app is an updated system app.
 * @property name Name of the app.
 * @property packageName Package name of the app.
 * @property eventType Type of app usage event (e.g., "foreground", "background", "launch", "close").
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class AppUsageEventSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val isSystemApp: Boolean,
    val isUpdatedSystemApp: Boolean,
    val name: String,
    val packageName: String,
    val eventType: String,
    val received: Long,
    @SerialName("device_type")
    val deviceType: String
)

