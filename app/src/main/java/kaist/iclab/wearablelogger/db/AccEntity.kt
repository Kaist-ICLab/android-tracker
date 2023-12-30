package kaist.iclab.wearablelogger.db

import androidx.room.Entity
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "accEvent",
    primaryKeys = ["timestamp"]
)
data class AccEntity(
    val timestamp: Long,
    val accData : Int,
)
