package kaist.iclab.mobiletracker.db.entity.phone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BatteryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val connectedType: Int,
    val status: Int,
    val level: Int,
    val temperature: Int
)
