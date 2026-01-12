package kaist.iclab.mobiletracker.db.entity.phone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val startTime: Long,
    val endTime: Long,
    val steps: Long
)

