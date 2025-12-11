package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.tracker.sensor.phone.BatterySensor

@Dao
interface BatteryDao: BaseDao<BatterySensor.Entity, BatteryEntity> {
    override suspend fun insert(sensorEntity: BatterySensor.Entity) {
        val entity = BatteryEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            connectedType = sensorEntity.connectedType,
            status = sensorEntity.status,
            level = sensorEntity.level,
            temperature = sensorEntity.temperature
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(batteryEntity: BatteryEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<BatteryEntity>)

    override suspend fun insertBatch(entities: List<BatterySensor.Entity>) {
        val roomEntities = entities.map { entity ->
            BatteryEntity(
                received = entity.received,
                timestamp = entity.timestamp,
                connectedType = entity.connectedType,
                status = entity.status,
                level = entity.level,
                temperature = entity.temperature
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM BatteryEntity ORDER BY timestamp ASC")
    suspend fun getAllBatteryData(): List<BatteryEntity>

    @Query("SELECT * FROM BatteryEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<BatteryEntity>

    @Query("SELECT MAX(timestamp) FROM BatteryEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM BatteryEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM BatteryEntity")
    suspend fun deleteAllBatteryData()

    override suspend fun deleteAll() {
        deleteAllBatteryData()
    }
}

