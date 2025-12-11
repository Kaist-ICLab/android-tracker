package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.entity.CallLogEntity
import kaist.iclab.tracker.sensor.phone.CallLogSensor

@Dao
interface CallLogDao: BaseDao<CallLogSensor.Entity, CallLogEntity> {
    override suspend fun insert(sensorEntity: CallLogSensor.Entity) {
        val entity = CallLogEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            duration = sensorEntity.duration,
            number = sensorEntity.number,
            type = sensorEntity.type
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(callLogEntity: CallLogEntity)

    @Query("SELECT * FROM CallLogEntity ORDER BY timestamp ASC")
    suspend fun getAllCallLogData(): List<CallLogEntity>

    @Query("SELECT * FROM CallLogEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<CallLogEntity>

    @Query("SELECT MAX(timestamp) FROM CallLogEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM CallLogEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM CallLogEntity")
    suspend fun deleteAllCallLogData()

    override suspend fun deleteAll() {
        deleteAllCallLogData()
    }
}

