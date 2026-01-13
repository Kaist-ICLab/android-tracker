package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing user interaction sensor data from the phone device.
 *
 * @property uuid User UUID.
 * @property timestamp Unix timestamp in milliseconds when the interaction was recorded.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 * @property deviceType Type of device (0 = phone, 1 = watch).
 * @property packageName Package name of the app where the interaction occurred.
 * @property className Class name (Activity/View) where the interaction occurred.
 * @property eventType Accessibility event type (raw Android event type int).
 * @property text Text associated with the event (e.g., content description, view text).
 */
@Serializable
data class UserInteractionSensorData(
    val uuid: String? = null,
    @SerialName("event_id")
    val eventId: String,
    val timestamp: String,
    val received: String,
    @SerialName("device_type")
    val deviceType: Int,
    @SerialName("package_name")
    val packageName: String,
    @SerialName("class_name")
    val className: String,
    @SerialName("event_type")
    val eventType: Int,
    val text: String
)

