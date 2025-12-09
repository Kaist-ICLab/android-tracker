package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing log sensor data from the phone device.
 *
 * @property uuid Unique identifier for the log sensor entry. Auto-generated when inserting into Supabase.
 * @property email Email address associated with the log entry.
 * @property message Log message content.
 * @property tag Tag for categorizing the log entry.
 * @property timestamp Unix timestamp in milliseconds when the log was recorded.
 */
@Serializable
data class LogSensorData(
    val uuid: String? = null,
    val email: String,
    val message: String,
    val tag: String,
    val timestamp: Long
)

