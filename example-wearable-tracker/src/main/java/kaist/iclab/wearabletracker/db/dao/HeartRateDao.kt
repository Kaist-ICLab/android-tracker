package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.HeartRateSensor
import kaist.iclab.wearabletracker.db.entity.HeartRateEntity
import kaist.iclab.wearabletracker.db.summary.HeartRateSummary

@Dao
interface HeartRateDao: BaseDao<HeartRateSensor.Entity, HeartRateSummary> {
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

    @Query("""
        SELECT 
            (timestamp / 600000) * 600000 AS bucketStart,
            COUNT(*) AS count,
            AVG(hr) AS avg,
            AVG(hr * hr) - AVG(hr) * AVG(hr) AS variance,
            SUM(CASE WHEN hrStatus != 1 THEN 1 ELSE 0 END) AS badStatusCount
        FROM heart_rate
        WHERE bucketStart BETWEEN :startTime and :endTime
        GROUP BY bucketStart
        ORDER BY bucketStart
    """)
    override suspend fun getSummaryBetween(startTime: Long, endTime: Long): List<HeartRateSummary>
}