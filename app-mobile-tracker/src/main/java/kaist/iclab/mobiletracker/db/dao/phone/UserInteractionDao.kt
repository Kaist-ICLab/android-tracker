package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.phone.UserInteractionEntity
import kaist.iclab.tracker.sensor.phone.UserInteractionSensor

@Dao
interface UserInteractionDao : BaseDao<UserInteractionSensor.Entity, UserInteractionEntity> {
    override suspend fun insert(sensorEntity: UserInteractionSensor.Entity, userUuid: String?) {
        val uuid = userUuid ?: ""
        val entity = UserInteractionEntity(
            uuid = uuid,
            eventId = sensorEntity.eventId,
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            packageName = sensorEntity.packageName,
            className = sensorEntity.className,
            eventType = sensorEntity.eventType,
            text = sensorEntity.text
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(entity: UserInteractionEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<UserInteractionEntity>)

    override suspend fun insertBatch(entities: List<UserInteractionSensor.Entity>, userUuid: String?) {
        val uuid = userUuid ?: ""
        val roomEntities = entities.map { e ->
            UserInteractionEntity(
                uuid = uuid,
                eventId = e.eventId,
                received = e.received,
                timestamp = e.timestamp,
                packageName = e.packageName,
                className = e.className,
                eventType = e.eventType,
                text = e.text
            )
        }

        if (roomEntities.isNotEmpty()) {
            insertBatchUsingRoomEntity(roomEntities)
        }
    }

    @Query("SELECT * FROM UserInteractionEntity ORDER BY timestamp ASC")
    suspend fun getAllUserInteractionData(): List<UserInteractionEntity>

    @Query("SELECT * FROM UserInteractionEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<UserInteractionEntity>

    @Query("SELECT MAX(timestamp) FROM UserInteractionEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM UserInteractionEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM UserInteractionEntity")
    suspend fun deleteAllUserInteractionData()

    override suspend fun deleteAll() {
        deleteAllUserInteractionData()
    }
}

