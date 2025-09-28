package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PPGEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val green: Int,
    val red: Int,
    val ir: Int,
    val greenStatus: Int,
    val redStatus: Int,
    val irStatus: Int,
)
