package kaist.iclab.mobiletracker.db.entity.phone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppListChangeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val changedAppJson: String?, // Serialized AppInfo as JSON
    val appListJson: String? // Serialized List<AppInfo> as JSON
)

