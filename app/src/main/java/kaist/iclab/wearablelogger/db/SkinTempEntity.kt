package kaist.iclab.wearablelogger.db

import androidx.room.Entity
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "skinTempEvent",
    primaryKeys = ["timestamp"]
)
data class SkinTempEntity(
    val timestamp: Long,
    val skinTempData : Int,
)
