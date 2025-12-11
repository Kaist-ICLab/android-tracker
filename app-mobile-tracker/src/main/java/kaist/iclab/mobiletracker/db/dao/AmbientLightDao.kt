package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor

@Dao
interface AmbientLightDao: BaseDao<AmbientLightSensor.Entity, AmbientLightEntity> {
    override suspend fun insert(sensorEntity: AmbientLightSensor.Entity) {
        val entity = AmbientLightEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            accuracy = sensorEntity.accuracy,
            value = sensorEntity.value
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(ambientLightEntity: AmbientLightEntity)

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
