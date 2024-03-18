package kaist.iclab.wearablelogger.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "recent")
data class RecentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long =0 ,
    val timestamp: Long,
    val acc: String,
    val ppg: String,
    val hr: String
)
