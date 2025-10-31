package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.PPGSensor
import kaist.iclab.wearabletracker.db.entity.PPGEntity

@Dao
interface PPGDao: BaseDao<PPGSensor.Entity> {
    override suspend fun insert(sensorEntity: PPGSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            PPGEntity(
                received = it.received,
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

    @Query("SELECT * FROM PPGEntity ORDER BY timestamp ASC")
    suspend fun getAllPPGData(): List<PPGEntity>

    @Query("DELETE FROM PPGEntity")
    suspend fun deleteAllPPGData()

    override suspend fun deleteAll() {
        deleteAllPPGData()
    }
}