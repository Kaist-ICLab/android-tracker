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
    val ppgData : List<Int>,
)

//class Converters {
//    @TypeConverter
//    fun listToJson(value: List<Int>) = Gson().toJson(value)
//
//    @TypeConverter
//    fun jsonTOList(value: String) = Gson().fromJson(value, Array<Int>::class.java).toList()
//}