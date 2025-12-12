package kaist.iclab.mobiletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataTrafficEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val received: Long,
    val timestamp: Long,
    val totalRx: Long,
    val totalTx: Long,
    val mobileRx: Long,
    val mobileTx: Long
)

