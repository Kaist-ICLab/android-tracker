package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.ScreenEntity
import kaist.iclab.tracker.sensor.phone.ScreenSensor

@Dao
interface ScreenDao: BaseDao<ScreenSensor.Entity, ScreenEntity> {
    override suspend fun insert(sensorEntity: ScreenSensor.Entity) {
        val entity = ScreenEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            type = sensorEntity.type
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(screenEntity: ScreenEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<ScreenEntity>)

    override suspend fun insertBatch(entities: List<ScreenSensor.Entity>) {
        val roomEntities = entities.map { entity ->
            ScreenEntity(
                received = entity.received,
                timestamp = entity.timestamp,
                type = entity.type
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM ScreenEntity ORDER BY timestamp ASC")
    suspend fun getAllScreenData(): List<ScreenEntity>

    @Query("SELECT * FROM ScreenEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<ScreenEntity>

    @Query("SELECT MAX(timestamp) FROM ScreenEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM ScreenEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM ScreenEntity")
    suspend fun deleteAllScreenData()

    override suspend fun deleteAll() {
        deleteAllScreenData()
    }
}
