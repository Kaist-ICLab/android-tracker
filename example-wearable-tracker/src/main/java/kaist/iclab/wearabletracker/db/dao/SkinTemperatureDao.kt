package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.SkinTemperatureSensor
import kaist.iclab.wearabletracker.db.entity.SkinTemperatureEntity

@Dao
interface SkinTemperatureDao: BaseDao<SkinTemperatureSensor.Entity> {
    override suspend fun insert(entity: SkinTemperatureSensor.Entity) {
        val entity = SkinTemperatureEntity(
            received = entity.received,
            timestamp = entity.timestamp,
            objectTemperature = entity.objectTemperature,
            ambientTemperature = entity.ambientTemperature,
            status = entity.status
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(skinTemperatureEntity: SkinTemperatureEntity)
}