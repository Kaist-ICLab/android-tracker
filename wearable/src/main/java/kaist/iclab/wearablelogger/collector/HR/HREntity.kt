package kaist.iclab.wearablelogger.collector.HR

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "hrEvent",
)
data class HREntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val dataReceived: Long,
    val timestamp: Long,
    val hr: Int,
    val hrStatus: Int,
    val ibi: List<Int>,
    val ibiStatus: List<Int>
)
