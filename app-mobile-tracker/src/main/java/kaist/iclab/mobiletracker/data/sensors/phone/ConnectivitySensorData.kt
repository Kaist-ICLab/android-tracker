package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing connectivity sensor data from the phone device.
 *
 * @property uuid Unique identifier for the connectivity sensor entry. Auto-generated when inserting into Supabase.
 * @property timestamp Unix timestamp in milliseconds when the connectivity status was recorded.
 * @property isConnected Whether the device is connected to a network.
 * @property connectionType Type of connection (e.g., "wifi", "mobile", "ethernet", "none").
 */
@Serializable
data class ConnectivitySensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val isConnected: Boolean,
    val connectionType: String
)

