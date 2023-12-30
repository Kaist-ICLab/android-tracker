package kaist.iclab.wearablelogger.db

import androidx.room.Entity
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "hribiEvent",
    primaryKeys = ["timestamp"]
)
data class HRIBIEntity(
    val timestamp: Long,
    val hribiData : String,
)
