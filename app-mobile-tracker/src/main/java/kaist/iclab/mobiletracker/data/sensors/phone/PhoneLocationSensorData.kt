package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing location sensor data from the phone device.
 *
 * @property uuid Unique identifier for the location sensor entry. Auto-generated when inserting into Supabase.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property timestamp Unix timestamp in milliseconds when the location was recorded.
 * @property accuracy Location accuracy in meters.
 * @property altitude Altitude in meters above sea level.
 * @property latitude Latitude coordinate in degrees.
 * @property longitude Longitude coordinate in degrees.
 * @property speed Speed in meters per second.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class PhoneLocationSensorData(
    val uuid: String? = null,
    @SerialName("device_type")
    val deviceType: String,
    val timestamp: Long,
    val accuracy: Float,
    val altitude: Float,
    val latitude: Float,
    val longitude: Float,
    val speed: Float,
    val received: Long
)

