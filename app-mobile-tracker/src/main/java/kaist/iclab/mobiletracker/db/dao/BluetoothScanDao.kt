package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.tracker.sensor.phone.BluetoothScanSensor

@Dao
interface BluetoothScanDao: BaseDao<BluetoothScanSensor.Entity, BluetoothScanEntity> {
    override suspend fun insert(sensorEntity: BluetoothScanSensor.Entity) {
        val entity = BluetoothScanEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            name = sensorEntity.name,
            alias = sensorEntity.alias,
            address = sensorEntity.address,
            bondState = sensorEntity.bondState,
            connectionType = sensorEntity.connectionType,
            classType = sensorEntity.classType,
            rssi = sensorEntity.rssi,
            isLE = sensorEntity.isLE
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(bluetoothScanEntity: BluetoothScanEntity)

    @Query("SELECT * FROM BluetoothScanEntity ORDER BY timestamp ASC")
    suspend fun getAllBluetoothScanData(): List<BluetoothScanEntity>

    @Query("SELECT * FROM BluetoothScanEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<BluetoothScanEntity>

    @Query("SELECT MAX(timestamp) FROM BluetoothScanEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM BluetoothScanEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM BluetoothScanEntity")
    suspend fun deleteAllBluetoothScanData()

    override suspend fun deleteAll() {
        deleteAllBluetoothScanData()
    }
}

