package kaist.iclab.mobiletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WifiScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val ssid: String,
    val bssid: String,
    val frequency: Int,
    val level: Int
)

