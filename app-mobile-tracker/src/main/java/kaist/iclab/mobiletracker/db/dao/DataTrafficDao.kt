package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.entity.DataTrafficEntity
import kaist.iclab.tracker.sensor.phone.DataTrafficStatSensor

@Dao
interface DataTrafficDao: BaseDao<DataTrafficStatSensor.Entity> {
    override suspend fun insert(sensorEntity: DataTrafficStatSensor.Entity) {
        val entity = DataTrafficEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            totalRx = sensorEntity.totalRx,
            totalTx = sensorEntity.totalTx,
            mobileRx = sensorEntity.mobileRx,
            mobileTx = sensorEntity.mobileTx
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(dataTrafficEntity: DataTrafficEntity)

    @Query("SELECT * FROM DataTrafficEntity ORDER BY timestamp ASC")
    suspend fun getAllDataTrafficData(): List<DataTrafficEntity>

    @Query("SELECT * FROM DataTrafficEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    suspend fun getDataTrafficDataAfterTimestamp(afterTimestamp: Long): List<DataTrafficEntity>

    @Query("SELECT MAX(timestamp) FROM DataTrafficEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM DataTrafficEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM DataTrafficEntity")
    suspend fun deleteAllDataTrafficData()

    override suspend fun deleteAll() {
        deleteAllDataTrafficData()
    }
}

