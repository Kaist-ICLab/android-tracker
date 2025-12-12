package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing data traffic sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property timestamp Unix timestamp in milliseconds when the data traffic was recorded.
 * @property totalRx Total received bytes (cumulative).
 * @property totalTx Total transmitted bytes (cumulative).
 * @property mobileRx Mobile received bytes (cumulative).
 * @property mobileTx Mobile transmitted bytes (cumulative).
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class DataTrafficSensorData(
    val uuid: String? = null,
    val deviceType: String,
    val received: Long,
    val timestamp: Long,
    val totalRx: Long,
    val totalTx: Long,
    val mobileRx: Long,
    val mobileTx: Long
)

