package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.HeartRateSensor
import kaist.iclab.wearabletracker.db.entity.HeartRateEntity

@Dao
interface HeartRateDao: BaseDao<HeartRateSensor.Entity> {
    override suspend fun insert(entity: HeartRateSensor.Entity) {
        val entity = HeartRateEntity(
            received = entity.received,
            timestamp = entity.timestamp,
            hr = entity.hr,
            hrStatus = entity.hrStatus,
            ibi = entity.ibi,
            ibiStatus = entity.ibiStatus
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(heartRateEntity: HeartRateEntity)
}