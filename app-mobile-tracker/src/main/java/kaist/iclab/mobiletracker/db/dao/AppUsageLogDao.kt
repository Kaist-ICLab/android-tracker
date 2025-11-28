package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.entity.AppUsageLogEntity
import kaist.iclab.tracker.sensor.phone.AppUsageLogSensor

@Dao
interface AppUsageLogDao: BaseDao<AppUsageLogSensor.Entity> {
    override suspend fun insert(sensorEntity: AppUsageLogSensor.Entity) {
        val entity = AppUsageLogEntity(
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

    @Query("SELECT * FROM AppUsageLogEntity ORDER BY timestamp ASC")
    suspend fun getAllAppUsageLogData(): List<AppUsageLogEntity>

    @Query("DELETE FROM AppUsageLogEntity")
    suspend fun deleteAllAppUsageLogData()

    override suspend fun deleteAll() {
        deleteAllAppUsageLogData()
    }
}

