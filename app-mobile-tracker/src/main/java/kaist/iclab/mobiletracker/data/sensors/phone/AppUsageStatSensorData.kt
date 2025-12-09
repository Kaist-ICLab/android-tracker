package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing app usage statistics sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property timestamp Timestamp in "YYYY-MM-DD HH:mm:ss" format when the app usage data was recorded.
 * @property endTime Unix timestamp in milliseconds when the app usage period ended.
 * @property isSystemApp Whether the app is a system app.
 * @property isUpdatedSystemApp Whether the app is an updated system app.
 * @property lastTimeUsed Unix timestamp in milliseconds when the app was last used.
 * @property name Name of the app.
 * @property packageName Package name of the app.
 * @property startTime Unix timestamp in milliseconds when the app usage period started.
 * @property totalTimeForeground Total time the app was in foreground in milliseconds.
 */
@Serializable
data class AppUsageStatSensorData(
    val uuid: String? = null,
    val timestamp: String,
    val endTime: Long,
    val isSystemApp: Boolean,
    val isUpdatedSystemApp: Boolean,
    val lastTimeUsed: Long,
    val name: String,
    val packageName: String,
    val startTime: Long,
    val totalTimeForeground: Long
)

