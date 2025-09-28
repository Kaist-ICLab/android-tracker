package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.galaxywatch.EDASensor
import kaist.iclab.wearabletracker.db.entity.EDAEntity

@Dao
interface EDADao: BaseDao<EDASensor.Entity> {
    override suspend fun insert(entity: EDASensor.Entity) {
        val entity = EDAEntity(
            received = entity.received,
            timestamp = entity.timestamp,
            skinConductance = entity.skinConductance,
            status = entity.status
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(edaEntity: EDAEntity)
}