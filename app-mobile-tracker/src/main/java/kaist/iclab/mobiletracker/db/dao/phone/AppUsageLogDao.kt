package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.phone.AppUsageLogEntity
import kaist.iclab.tracker.sensor.phone.AppUsageLogSensor

@Dao
interface AppUsageLogDao: BaseDao<AppUsageLogSensor.Entity, AppUsageLogEntity> {
    override suspend fun insert(sensorEntity: AppUsageLogSensor.Entity, userUuid: String?) {
        val entity = AppUsageLogEntity(
            uuid = userUuid ?: "",
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            packageName = sensorEntity.packageName,
            installedBy = sensorEntity.installedBy,
            eventType = sensorEntity.eventType
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(appUsageLogEntity: AppUsageLogEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<AppUsageLogEntity>)

    override suspend fun insertBatch(entities: List<AppUsageLogSensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            AppUsageLogEntity(
                uuid = userUuid ?: "",
                received = entity.received,
                timestamp = entity.timestamp,
                packageName = entity.packageName,
                installedBy = entity.installedBy,
                eventType = entity.eventType
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM AppUsageLogEntity ORDER BY timestamp ASC")
    suspend fun getAllAppUsageLogData(): List<AppUsageLogEntity>

    @Query("SELECT * FROM AppUsageLogEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<AppUsageLogEntity>

    @Query("SELECT MAX(timestamp) FROM AppUsageLogEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM AppUsageLogEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM AppUsageLogEntity")
    suspend fun deleteAllAppUsageLogData()

    override suspend fun deleteAll() {
        deleteAllAppUsageLogData()
    }
}
