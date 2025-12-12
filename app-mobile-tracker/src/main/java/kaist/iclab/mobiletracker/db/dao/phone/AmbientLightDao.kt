package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor

@Dao
interface AmbientLightDao: BaseDao<AmbientLightSensor.Entity, AmbientLightEntity> {
    override suspend fun insert(sensorEntity: AmbientLightSensor.Entity, userUuid: String?) {
        val entity = AmbientLightEntity(
            uuid = userUuid ?: "",
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            accuracy = sensorEntity.accuracy,
            value = sensorEntity.value
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(ambientLightEntity: AmbientLightEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<AmbientLightEntity>)

    override suspend fun insertBatch(entities: List<AmbientLightSensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            AmbientLightEntity(
                uuid = userUuid ?: "",
                received = entity.received,
                timestamp = entity.timestamp,
                accuracy = entity.accuracy,
                value = entity.value
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM AmbientLightEntity ORDER BY timestamp ASC")
    suspend fun getAllAmbientLightData(): List<AmbientLightEntity>

    @Query("SELECT * FROM AmbientLightEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<AmbientLightEntity>

    @Query("SELECT MAX(timestamp) FROM AmbientLightEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM AmbientLightEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM AmbientLightEntity")
    suspend fun deleteAllAmbientLightData()

    override suspend fun deleteAll() {
        deleteAllAmbientLightData()
    }
}

