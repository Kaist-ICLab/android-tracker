package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.phone.UserInteractionEntity
import kaist.iclab.tracker.sensor.phone.UserInteractionSensor

@Dao
interface UserInteractionDao : BaseDao<UserInteractionSensor.Entity, UserInteractionEntity> {
    @Query("SELECT * FROM UserInteractionEntity WHERE uuid = :uuid ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastForUser(uuid: String): UserInteractionEntity?

    override suspend fun insert(sensorEntity: UserInteractionSensor.Entity, userUuid: String?) {
        val uuid = userUuid ?: ""
        // Skip UNKNOWN package/class
        if (sensorEntity.packageName == "UNKNOWN" || sensorEntity.className == "UNKNOWN") return

        // Skip if identical to last stored entry for this user
        val last = getLastForUser(uuid)
        if (last != null &&
            last.packageName == sensorEntity.packageName &&
            last.className == sensorEntity.className &&
            last.eventType == sensorEntity.eventType &&
            last.text == sensorEntity.text
        ) {
            return
        }

        val entity = UserInteractionEntity(
            uuid = uuid,
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
        var last = getLastForUser(uuid)

        val roomEntities = mutableListOf<UserInteractionEntity>()
        entities.forEach { e ->
            // Skip UNKNOWN package/class
            if (e.packageName == "UNKNOWN" || e.className == "UNKNOWN") return@forEach

            // Skip if identical to last considered entry
            val isDuplicate = last != null &&
                last!!.packageName == e.packageName &&
                last!!.className == e.className &&
                last!!.eventType == e.eventType &&
                last!!.text == e.text

            if (!isDuplicate) {
                val entity = UserInteractionEntity(
                    uuid = uuid,
                    received = e.received,
                    timestamp = e.timestamp,
                    packageName = e.packageName,
                    className = e.className,
                    eventType = e.eventType,
                    text = e.text
                )
                roomEntities.add(entity)
                last = entity
            }
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

