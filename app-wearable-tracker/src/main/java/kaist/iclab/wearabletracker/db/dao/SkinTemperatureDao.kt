package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.SkinTemperatureSensor
import kaist.iclab.wearabletracker.db.entity.SkinTemperatureEntity

@Dao
interface SkinTemperatureDao: BaseDao<SkinTemperatureSensor.Entity> {
    override suspend fun insert(sensorEntity: SkinTemperatureSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            SkinTemperatureEntity(
                received = it.received,
                timestamp = it.timestamp,
                objectTemperature = it.objectTemperature,
                ambientTemperature = it.ambientTemperature,
                status = it.status
            )
        }
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(skinTemperatureEntity: List<SkinTemperatureEntity>)

    @Query("SELECT * FROM SkinTemperatureEntity ORDER BY timestamp ASC")
    suspend fun getAllSkinTemperatureData(): List<SkinTemperatureEntity>

    @Query("DELETE FROM SkinTemperatureEntity")
    suspend fun deleteAllSkinTemperatureData()

    override suspend fun deleteAll() {
        deleteAllSkinTemperatureData()
    }
}