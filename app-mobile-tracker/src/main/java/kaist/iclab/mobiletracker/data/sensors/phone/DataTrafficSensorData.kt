package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing data traffic sensor data from the phone device.
 *
 * @property uuid Unique identifier for the data traffic sensor entry. Auto-generated when inserting into Supabase.
 * @property timestamp Unix timestamp in milliseconds when the data traffic was recorded.
 * @property duration Duration of the data traffic measurement in milliseconds.
 * @property rxKiloBytes Received data in kilobytes.
 * @property txKiloBytes Transmitted data in kilobytes.
 */
@Serializable
data class DataTrafficSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val duration: Int,
    val rxKiloBytes: Int,
    val txKiloBytes: Int
)

