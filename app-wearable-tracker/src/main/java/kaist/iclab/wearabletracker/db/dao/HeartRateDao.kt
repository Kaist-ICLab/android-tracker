package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.HeartRateSensor
import kaist.iclab.wearabletracker.db.entity.HeartRateEntity

@Dao
interface HeartRateDao: BaseDao<HeartRateSensor.Entity> {
    override suspend fun insert(sensorEntity: HeartRateSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            HeartRateEntity(
                received = it.received,
                timestamp = it.timestamp,
                hr = it.hr,
                hrStatus = it.hrStatus,
                ibi = it.ibi,
                ibiStatus = it.ibiStatus
            )
        }
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(heartRateEntity: List<HeartRateEntity>)

    @Query("SELECT * FROM HeartRateEntity ORDER BY timestamp ASC")
    suspend fun getAllHeartRateData(): List<HeartRateEntity>

    @Query("DELETE FROM HeartRateEntity")
    suspend fun deleteAllHeartRateData()

    override suspend fun deleteAll() {
        deleteAllHeartRateData()
    }
}