package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "acceleration"
)
data class AccelerometerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val x: Float,
    val y: Float,
    val z: Float
)
