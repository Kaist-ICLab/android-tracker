package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing physical activity sensor data from the phone device.
 *
 * @property uuid Unique identifier for the physical activity entry. Auto-generated when inserting into Supabase.
 * @property timestamp Unix timestamp in milliseconds when the activity was recorded.
 * @property confidence Confidence level of the activity detection (0.0 to 1.0).
 * @property activityType Type of physical activity (e.g., "walking", "running", "still").
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class PhysicalActivitySensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val confidence: Float,
    val activityType: String,
    val received: Long
)

