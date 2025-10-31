package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.AccelerometerSensor
import kaist.iclab.wearabletracker.db.entity.AccelerometerEntity

@Dao
interface AccelerometerDao: BaseDao<AccelerometerSensor.Entity> {
    override suspend fun insert(sensorEntity: AccelerometerSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            AccelerometerEntity(
                received = it.received,
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

    @Query("SELECT * FROM AccelerometerEntity ORDER BY timestamp ASC")
    suspend fun getAllAccelerometerData(): List<AccelerometerEntity>

    @Query("DELETE FROM AccelerometerEntity")
    suspend fun deleteAllAccelerometerData()

    override suspend fun deleteAll() {
        deleteAllAccelerometerData()
    }
}