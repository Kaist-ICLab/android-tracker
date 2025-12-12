package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing call log sensor data from the phone device.
 *
 * @property uuid Unique identifier for the call log sensor entry. Auto-generated when inserting into Supabase.
 * @property timestamp Unix timestamp in milliseconds when the call was recorded.
 * @property duration Duration of the call in milliseconds.
 * @property number Phone number associated with the call.
 * @property type Type of call (e.g., CallLog.Calls.INCOMING_TYPE, OUTGOING_TYPE, MISSED_TYPE).
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 * @property deviceType Type of device (e.g., "phone", "watch").
 */
@Serializable
data class CallLogSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val duration: Long,
    val number: String,
    val type: Int,
    val received: Long,
    @SerialName("device_type")
    val deviceType: String
)

