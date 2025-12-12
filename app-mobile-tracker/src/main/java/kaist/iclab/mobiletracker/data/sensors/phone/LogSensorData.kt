package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing log sensor data from the phone device.
 *
 * @property uuid Unique identifier for the log sensor entry. Auto-generated when inserting into Supabase.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property email Email address associated with the log entry.
 * @property message Log message content.
 * @property tag Tag for categorizing the log entry.
 * @property timestamp Unix timestamp in milliseconds when the log was recorded.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class LogSensorData(
    val uuid: String? = null,
    @SerialName("device_type")
    val deviceType: String,
    val email: String,
    val message: String,
    val tag: String,
    val timestamp: Long,
    val received: Long
)

