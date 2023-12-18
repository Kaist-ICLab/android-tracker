package kaist.iclab.wearablelogger.db

import androidx.room.Entity
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "ppgEvent",
    primaryKeys = ["timestamp"]
)
data class PpgEntity(
    val timestamp: Long,
    val ppgData : Int,
)
