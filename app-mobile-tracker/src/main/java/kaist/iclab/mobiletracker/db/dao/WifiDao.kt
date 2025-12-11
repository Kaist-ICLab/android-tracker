package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.entity.WifiEntity
import kaist.iclab.tracker.sensor.phone.WifiScanSensor

@Dao
interface WifiDao: BaseDao<WifiScanSensor.Entity> {
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

    @Query("SELECT * FROM WifiEntity ORDER BY timestamp ASC")
    suspend fun getAllWifiData(): List<WifiEntity>

    @Query("SELECT * FROM WifiEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    suspend fun getWifiDataAfterTimestamp(afterTimestamp: Long): List<WifiEntity>

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

