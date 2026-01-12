package kaist.iclab.mobiletracker.db.entity.phone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val duration: Long,
    val number: String,
    val type: Int
)

