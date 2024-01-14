package kaist.iclab.wearablelogger.collector.PPGGreen

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

@Entity(
    tableName = "ppgEvent",
)
data class PpgEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 고유 ID
    val dataReceived: Long,
    val timestamp: Long,
    val ppg : Int,
    val status: Int
)
