package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing device event sensor data from the phone device.
 *
 * @property uuid Unique identifier for the device event sensor entry. Auto-generated when inserting into Supabase.
 * @property timestamp Unix timestamp in milliseconds when the device event was recorded.
 * @property eventType Type of device event (e.g., "screen_on", "screen_off", "battery_low").
 */
@Serializable
data class DeviceEventSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val eventType: String
)

