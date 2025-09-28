package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EDAEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val skinConductance: Float,
    val status: Int
)
