package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.AccelerometerSensor
import kaist.iclab.wearabletracker.db.entity.AccelerometerEntity

@Dao
interface AccelerometerDao: BaseDao<AccelerometerSensor.Entity> {
    override suspend fun insert(sensorEntity: AccelerometerSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            AccelerometerEntity(
                received = sensorEntity.received,
                timestamp = it.timestamp,
                x = it.x,
                y = it.y,
                z = it.z
            )
        }
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(accelerometerEntity: List<AccelerometerEntity>)
}