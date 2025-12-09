package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing ambient light sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property timestamp Timestamp in "YYYY-MM-DD HH:mm:ss" format when the ambient light data was recorded.
 * @property value Ambient light value in lux (illuminance).
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 * @property accuracy Sensor accuracy (0 = SENSOR_STATUS_UNRELIABLE, 1 = SENSOR_STATUS_ACCURACY_LOW, 2 = SENSOR_STATUS_ACCURACY_MEDIUM, 3 = SENSOR_STATUS_ACCURACY_HIGH).
 */
@Serializable
data class AmbientLightSensorData(
    val uuid: String? = null,
    val timestamp: String,
    val value: Float,
    val received: Long,
    val accuracy: Int
)

