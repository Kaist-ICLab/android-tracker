package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.tracker.sensor.phone.BatterySensor

@Dao
interface BatteryDao: BaseDao<BatterySensor.Entity> {
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

    @Query("SELECT * FROM BatteryEntity ORDER BY timestamp ASC")
    suspend fun getAllBatteryData(): List<BatteryEntity>

    @Query("DELETE FROM BatteryEntity")
    suspend fun deleteAllBatteryData()

    override suspend fun deleteAll() {
        deleteAllBatteryData()
    }
}
