package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing WiFi sensor data from the phone device.
 *
 * @property uuid UUID of the current logged in user.
 * @property timestamp Unix timestamp in milliseconds when the WiFi data was recorded.
 * @property bssid Basic Service Set Identifier (MAC address) of the WiFi access point.
 * @property frequency WiFi frequency in MHz.
 * @property rssi Received Signal Strength Indicator in dBm.
 * @property ssid Service Set Identifier (network name) of the WiFi access point.
 * @property received Timestamp when the data was received by the phone (Unix timestamp in milliseconds).
 */
@Serializable
data class WifiSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val bssid: String,
    val frequency: Int,
    val rssi: Int,
    val ssid: String,
    val received: Long
)

