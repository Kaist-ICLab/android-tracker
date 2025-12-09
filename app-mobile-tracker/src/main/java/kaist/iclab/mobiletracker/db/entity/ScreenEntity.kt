package kaist.iclab.mobiletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScreenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val type: String
)

