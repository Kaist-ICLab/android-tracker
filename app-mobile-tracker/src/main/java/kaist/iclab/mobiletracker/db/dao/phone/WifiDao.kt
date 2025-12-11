package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.WifiEntity
import kaist.iclab.tracker.sensor.phone.WifiScanSensor

@Dao
interface WifiDao: BaseDao<WifiScanSensor.Entity, WifiEntity> {
    override suspend fun insert(sensorEntity: WifiScanSensor.Entity) {
        val entity = WifiEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            ssid = sensorEntity.ssid,
            bssid = sensorEntity.bssid,
            frequency = sensorEntity.frequency,
            level = sensorEntity.level
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(wifiEntity: WifiEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<WifiEntity>)

    override suspend fun insertBatch(entities: List<WifiScanSensor.Entity>) {
        val roomEntities = entities.map { entity ->
            WifiEntity(
                received = entity.received,
                timestamp = entity.timestamp,
                ssid = entity.ssid,
                bssid = entity.bssid,
                frequency = entity.frequency,
                level = entity.level
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM WifiEntity ORDER BY timestamp ASC")
    suspend fun getAllWifiData(): List<WifiEntity>

    @Query("SELECT * FROM WifiEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<WifiEntity>

    @Query("SELECT MAX(timestamp) FROM WifiEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM WifiEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM WifiEntity")
    suspend fun deleteAllWifiData()

    override suspend fun deleteAll() {
        deleteAllWifiData()
    }
}

