package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccelerometerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val x: Float,
    val y: Float,
    val z: Float
) : CsvSerializable {
    override fun toCsvHeader(): String = "id,received,timestamp,x,y,z"
    override fun toCsvRow(): String = "$id,$received,$timestamp,$x,$y,$z"
}
