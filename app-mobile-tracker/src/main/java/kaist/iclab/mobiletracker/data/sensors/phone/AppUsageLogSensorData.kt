package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase data class representing app usage log sensor data from the phone device.
 *
 * @property uuid User UUID
 * @property timestamp Unix timestamp in milliseconds when the app usage event was recorded.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 * @property deviceType Type of device (0 = phone, 1 = watch).
 * @property packageName Package name of the app.
 * @property installedBy How the app was installed ("SYSTEM", "USER", or "UNKNOWN").
 * @property eventType Type of app usage event as integer (Android UsageEvents.Event constants).
 */
@Serializable
data class AppUsageLogSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val received: Long,
    @SerialName("device_type")
    val deviceType: Int,
    @SerialName("package_name")
    val packageName: String,
    @SerialName("installed_by")
    val installedBy: String,
    @SerialName("event_type")
    val eventType: Int
)

