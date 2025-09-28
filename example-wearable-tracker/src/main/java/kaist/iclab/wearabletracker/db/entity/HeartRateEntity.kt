package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val hr: Int,
    val hrStatus: Int,
    val ibi: List<Int>,
    val ibiStatus: List<Int>,
)
