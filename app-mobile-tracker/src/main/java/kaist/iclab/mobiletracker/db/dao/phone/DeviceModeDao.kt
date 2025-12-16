package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.phone.DeviceModeEntity
import kaist.iclab.tracker.sensor.phone.DeviceModeSensor

@Dao
interface DeviceModeDao: BaseDao<DeviceModeSensor.Entity, DeviceModeEntity> {
    override suspend fun insert(sensorEntity: DeviceModeSensor.Entity, userUuid: String?) {
        val entity = DeviceModeEntity(
            uuid = userUuid ?: "",
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            eventType = sensorEntity.eventType,
            value = sensorEntity.value
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(deviceModeEntity: DeviceModeEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<DeviceModeEntity>)

    override suspend fun insertBatch(entities: List<DeviceModeSensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            DeviceModeEntity(
                uuid = userUuid ?: "",
                received = entity.received,
                timestamp = entity.timestamp,
                eventType = entity.eventType,
                value = entity.value
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM DeviceModeEntity ORDER BY timestamp ASC")
    suspend fun getAllDeviceModeData(): List<DeviceModeEntity>

    @Query("SELECT * FROM DeviceModeEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<DeviceModeEntity>

    @Query("SELECT MAX(timestamp) FROM DeviceModeEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM DeviceModeEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM DeviceModeEntity")
    suspend fun deleteAllDeviceModeData()

    override suspend fun deleteAll() {
        deleteAllDeviceModeData()
    }
}
