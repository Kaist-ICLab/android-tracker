package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SkinTemperatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val objectTemperature: Float,
    val ambientTemperature: Float,
    val status: Int
)
