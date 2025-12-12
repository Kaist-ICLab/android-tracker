package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing record sensor data from the phone device.
 *
 * @property uuid Unique identifier for the record sensor entry. Auto-generated when inserting into Supabase.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property timestamp Unix timestamp in milliseconds when the record data was recorded.
 * @property channelMask Channel mask configuration for the audio recording.
 * @property duration Duration of the recording in milliseconds.
 * @property encoding Audio encoding format (e.g., "AAC", "MP3").
 * @property path File path where the recording is stored.
 * @property sampleRate Audio sample rate in Hz.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class RecordSensorData(
    val uuid: String? = null,
    val deviceType: String,
    val timestamp: Long,
    val channelMask: String,
    val duration: Int,
    val encoding: String,
    val path: String,
    val sampleRate: Int,
    val received: Long
)

