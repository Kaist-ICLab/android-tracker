package kaist.iclab.mobiletracker.data.sensors.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing message sensor data from the phone device.
 *
 * @property uuid Unique identifier for the message sensor entry. Auto-generated when inserting into Supabase.
 * @property timestamp Unix timestamp in milliseconds when the message data was recorded.
 * @property contact Contact name associated with the message.
 * @property isPinned Whether the message is pinned.
 * @property isStarred Whether the message is starred.
 * @property messageBox Type of message box (e.g., "inbox", "sent", "draft").
 * @property messageClass Classification of the message.
 * @property number Phone number associated with the message.
 * @property timesContacted Number of times this contact has been contacted.
 */
@Serializable
data class MessageSensorData(
    val uuid: String? = null,
    val timestamp: Long,
    val contact: String,
    val isPinned: Boolean,
    val isStarred: Boolean,
    val messageBox: String,
    val messageClass: String,
    val number: String,
    val timesContacted: Int
)

