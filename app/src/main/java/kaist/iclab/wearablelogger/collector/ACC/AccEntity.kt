package kaist.iclab.wearablelogger.collector.ACC

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "accEvent",
)
data class AccEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val dataReceived: Long,
    val timestamp: Long,
    val x : Float,
    val y : Float,
    val z : Float,
)
