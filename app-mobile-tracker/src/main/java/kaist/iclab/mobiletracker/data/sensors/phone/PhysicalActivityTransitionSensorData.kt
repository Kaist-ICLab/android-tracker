package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing physical activity transition sensor data from the phone device.
 *
 * @property uuid Unique identifier for the physical activity transition entry. Auto-generated when inserting into Supabase.
 * @property timestamp Unix timestamp in milliseconds when the transition was recorded.
 * @property transitionType Type of physical activity transition (e.g., "enter", "exit").
 */
@Serializable
data class PhysicalActivityTransitionSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val transitionType: String
)

