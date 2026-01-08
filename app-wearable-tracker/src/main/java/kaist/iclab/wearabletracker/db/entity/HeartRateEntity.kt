package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val received: Long,
    val timestamp: Long,
    val hr: Int,
    val hrStatus: Int,
    val ibi: List<Int>,
    val ibiStatus: List<Int>,
) : CsvSerializable {
    override fun toCsvHeader(): String = "id,received,timestamp,hr,hrStatus,ibi,ibiStatus"
    override fun toCsvRow(): String {
        val ibiString = ibi.joinToString(";")
        val ibiStatusString = ibiStatus.joinToString(";")
        return "$id,$received,$timestamp,$hr,$hrStatus,$ibiString,$ibiStatusString"
    }
}
