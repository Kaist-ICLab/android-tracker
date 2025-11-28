package kaist.iclab.mobiletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val name: String,
    val alias: String,
    val address: String,
    val bondState: Int,
    val connectionType: Int,
    val classType: Int,
    val rssi: Int,
    val isLE: Boolean
)

