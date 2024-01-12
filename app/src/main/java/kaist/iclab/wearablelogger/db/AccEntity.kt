package kaist.iclab.wearablelogger.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "accEvent",
)
data class AccEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val timestamp: Long,
    val accData : Int,
)
