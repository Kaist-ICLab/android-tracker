package kaist.iclab.mobiletracker.db.entity.phone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConnectivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val networkType: String,
    val isConnected: Boolean,
    val hasInternet: Boolean,
    val transportTypes: String
)

