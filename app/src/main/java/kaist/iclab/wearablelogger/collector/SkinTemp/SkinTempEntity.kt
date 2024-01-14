package kaist.iclab.wearablelogger.collector.SkinTemp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "skinTempEvent",
)
data class SkinTempEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val dataReceived: Long,
    val timestamp: Long,
    val ambientTemp: Float,
    val objectTemp: Float,
    val status: Int
)
