package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.PPGSensor
import kaist.iclab.wearabletracker.db.entity.PPGEntity

@Dao
interface PPGDao: BaseDao<PPGSensor.Entity> {
    override suspend fun insert(entity: PPGSensor.Entity) {
        val entity = PPGEntity(
            received = entity.received,
            timestamp = entity.timestamp,
            green = entity.green,
            red = entity.red,
            ir = entity.ir,
            greenStatus = entity.greenStatus,
            redStatus = entity.redStatus,
            irStatus = entity.irStatus,
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(ppgEntity: PPGEntity)
}