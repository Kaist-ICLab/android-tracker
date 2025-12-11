package kaist.iclab.mobiletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing location sensor data received from watch.
 * 
 * @property id Auto-generated primary key
 * @property received Timestamp when phone received the data from watch (milliseconds)
 * @property timestamp Timestamp when watch recorded the data (milliseconds)
 * @property latitude Latitude coordinate in degrees
 * @property longitude Longitude coordinate in degrees
 * @property altitude Altitude in meters above sea level
 * @property speed Speed in meters per second
 * @property accuracy Location accuracy in meters
 */
@Entity(tableName = "watch_location")
data class WatchLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val received: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val accuracy: Float
)

