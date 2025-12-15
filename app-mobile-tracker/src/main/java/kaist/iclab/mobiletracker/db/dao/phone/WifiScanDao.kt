package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.WifiScanEntity
import kaist.iclab.tracker.sensor.phone.WifiScanSensor

@Dao
interface WifiScanDao: BaseDao<WifiScanSensor.Entity, WifiScanEntity> {
    override suspend fun insert(sensorEntity: WifiScanSensor.Entity, userUuid: String?) {
        val entity = WifiScanEntity(
            uuid = userUuid ?: "",
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
    suspend fun insertUsingRoomEntity(wifiScanEntity: WifiScanEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<WifiScanEntity>)

    override suspend fun insertBatch(entities: List<WifiScanSensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            WifiScanEntity(
                uuid = userUuid ?: "",
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

    @Query("SELECT * FROM WifiScanEntity ORDER BY timestamp ASC")
    suspend fun getAllWifiData(): List<WifiScanEntity>

    @Query("SELECT * FROM WifiScanEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<WifiScanEntity>

    @Query("SELECT MAX(timestamp) FROM WifiScanEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM WifiScanEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM WifiScanEntity")
    suspend fun deleteAllWifiData()

    override suspend fun deleteAll() {
        deleteAllWifiData()
    }
}

