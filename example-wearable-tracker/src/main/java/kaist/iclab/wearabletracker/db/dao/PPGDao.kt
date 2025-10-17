package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.PPGSensor
import kaist.iclab.wearabletracker.db.entity.PPGEntity

@Dao
interface PPGDao: BaseDao<PPGSensor.Entity> {
    override suspend fun insert(sensorEntity: PPGSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            PPGEntity(
                received = sensorEntity.received,
                timestamp = it.timestamp,
                green = it.green,
                red = it.red,
                ir = it.ir,
                greenStatus = it.greenStatus,
                redStatus = it.redStatus,
                irStatus = it.irStatus,
            )
        }

        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(ppgEntity: List<PPGEntity>)
}