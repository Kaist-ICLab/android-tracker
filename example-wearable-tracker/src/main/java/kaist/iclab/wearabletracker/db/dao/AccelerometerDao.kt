package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.AccelerometerSensor
import kaist.iclab.wearabletracker.db.entity.AccelerometerEntity
import kaist.iclab.wearabletracker.db.summary.AccelerometerSummary

@Dao
interface AccelerometerDao: BaseDao<AccelerometerSensor.Entity, AccelerometerSummary> {
    override suspend fun insert(sensorEntity: AccelerometerSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            AccelerometerEntity(
                received = it.received,
                timestamp = it.timestamp,
                x = it.x,
                y = it.y,
                z = it.z
            )
        }
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(accelerometerEntity: List<AccelerometerEntity>)

    @Query("""
        SELECT 
            (timestamp / 600000) * 600000 AS bucketStart,
            COUNT(*) AS count,
            AVG(x * x + y * y + z * z) AS avg,
            AVG((x * x + y * y + z * z) * (x * x + y * y + z * z)) - AVG(x * x + y * y + z * z) * AVG(x * x + y * y + z * z) AS variance
        FROM acceleration
        WHERE bucketStart BETWEEN :startTime AND :endTime
        GROUP BY bucketStart
        ORDER BY bucketStart
    """)
    override suspend fun getSummaryBetween(startTime: Long, endTime: Long): List<AccelerometerSummary>
}