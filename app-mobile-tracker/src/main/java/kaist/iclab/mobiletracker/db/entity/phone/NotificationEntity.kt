package kaist.iclab.mobiletracker.db.entity.phone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val packageName: String,
    val eventType: String,
    val title: String,
    val text: String,
    val visibility: Int,
    val category: String
)
