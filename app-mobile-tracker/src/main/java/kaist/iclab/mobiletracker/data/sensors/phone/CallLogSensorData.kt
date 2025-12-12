package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing call log sensor data from the phone device.
 *
 * @property uuid Unique identifier for the call log sensor entry. Auto-generated when inserting into Supabase.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property timestamp Unix timestamp in milliseconds when the call was recorded.
 * @property contact Contact name associated with the call.
 * @property dataUsage Data usage during the call in bytes.
 * @property duration Duration of the call in seconds.
 * @property isPinned Whether the call log entry is pinned.
 * @property isStarred Whether the call log entry is starred.
 * @property number Phone number associated with the call.
 * @property presentation Call presentation type (e.g., "allowed", "restricted", "unknown").
 * @property timesContacted Number of times this contact has been contacted.
 * @property callType Type of call (e.g., "incoming", "outgoing", "missed").
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class CallLogSensorData(
    val uuid: String? = null,
    @SerialName("device_type")
    val deviceType: String,
    val timestamp: Long,
    val contact: String,
    val dataUsage: Int,
    val duration: Int,
    val isPinned: Boolean,
    val isStarred: Boolean,
    val number: String,
    val presentation: String,
    val timesContacted: Int,
    val callType: String,
    val received: Long
)

