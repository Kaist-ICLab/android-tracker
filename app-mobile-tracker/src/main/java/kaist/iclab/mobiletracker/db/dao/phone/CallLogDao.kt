package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.phone.CallLogEntity
import kaist.iclab.tracker.sensor.phone.CallLogSensor

@Dao
interface CallLogDao: BaseDao<CallLogSensor.Entity, CallLogEntity> {
    override suspend fun insert(sensorEntity: CallLogSensor.Entity, userUuid: String?) {
        val entity = CallLogEntity(
            uuid = userUuid ?: "",
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

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<CallLogEntity>)

    override suspend fun insertBatch(entities: List<CallLogSensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            CallLogEntity(
                uuid = userUuid ?: "",
                received = entity.received,
                timestamp = entity.timestamp,
                duration = entity.duration,
                number = entity.number,
                type = entity.type
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

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

    @Query("SELECT COUNT(*) FROM CallLogEntity WHERE timestamp >= :afterTimestamp")
    fun getDailyCallLogCount(afterTimestamp: Long): kotlinx.coroutines.flow.Flow<Int>

    override suspend fun deleteAll() {
        deleteAllCallLogData()
    }
}
