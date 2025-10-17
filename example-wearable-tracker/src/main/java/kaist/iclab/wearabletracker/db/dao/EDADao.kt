package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.EDASensor
import kaist.iclab.wearabletracker.db.entity.EDAEntity

@Dao
interface EDADao: BaseDao<EDASensor.Entity> {
    override suspend fun insert(sensorEntity: EDASensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            EDAEntity(
                received = sensorEntity.received,
                timestamp = it.timestamp,
                skinConductance = it.skinConductance,
                status = it.status
            )
        }

        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(edaEntity: List<EDAEntity>)
}