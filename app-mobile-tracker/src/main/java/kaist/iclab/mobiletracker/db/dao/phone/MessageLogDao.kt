package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.phone.MessageLogEntity
import kaist.iclab.tracker.sensor.phone.MessageLogSensor

@Dao
interface MessageLogDao : BaseDao<MessageLogSensor.Entity, MessageLogEntity> {
    override suspend fun insert(sensorEntity: MessageLogSensor.Entity, userUuid: String?) {
        val entity = MessageLogEntity(
            uuid = userUuid ?: "",
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            number = sensorEntity.number,
            messageType = sensorEntity.messageType,
            contactType = sensorEntity.contactType
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(messageLogEntity: MessageLogEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<MessageLogEntity>)

    override suspend fun insertBatch(entities: List<MessageLogSensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            MessageLogEntity(
                uuid = userUuid ?: "",
                received = entity.received,
                timestamp = entity.timestamp,
                number = entity.number,
                messageType = entity.messageType,
                contactType = entity.contactType
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM MessageLogEntity ORDER BY timestamp ASC")
    suspend fun getAllMessageLogData(): List<MessageLogEntity>

    @Query("SELECT * FROM MessageLogEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<MessageLogEntity>

    @Query("SELECT MAX(timestamp) FROM MessageLogEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM MessageLogEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM MessageLogEntity")
    suspend fun deleteAllMessageLogData()

    override suspend fun deleteAll() {
        deleteAllMessageLogData()
    }
}
