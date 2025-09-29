package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.AccelerometerSensor
import kaist.iclab.wearabletracker.db.entity.AccelerometerEntity

@Dao
interface AccelerometerDao: BaseDao<AccelerometerSensor.Entity> {
    override suspend fun insert(entity: AccelerometerSensor.Entity) {
        val entity = AccelerometerEntity(
            received = entity.received,
            timestamp = entity.timestamp,
            x = entity.x,
            y = entity.y,
            z = entity.z
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(accelerometerEntity: AccelerometerEntity)
}