package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing media sensor data from the phone device.
 *
 * @property uuid Unique identifier for the media sensor entry. Auto-generated when inserting into Supabase.
 * @property deviceType Type of device (e.g., "phone", "watch").
 * @property timestamp Unix timestamp in milliseconds when the media data was recorded.
 * @property bucketDisplay Display name of the media bucket/folder.
 * @property mimetype MIME type of the media file (e.g., "image/jpeg", "video/mp4").
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class MediaSensorData(
    val uuid: String? = null,
    @SerialName("device_type")
    val deviceType: String,
    val timestamp: Long,
    val bucketDisplay: String,
    val mimetype: String,
    val received: Long
)

