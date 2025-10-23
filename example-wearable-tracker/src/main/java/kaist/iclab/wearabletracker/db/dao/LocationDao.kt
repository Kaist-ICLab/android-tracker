package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.common.LocationSensor
import kaist.iclab.wearabletracker.db.entity.LocationEntity
import kaist.iclab.wearabletracker.db.summary.LocationSummary

@Dao
interface LocationDao: BaseDao<LocationSensor.Entity, LocationSummary> {
    override suspend fun insert(sensorEntity: LocationSensor.Entity) {
        val entity = LocationEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            latitude = sensorEntity.latitude,
            longitude = sensorEntity.longitude,
            altitude = sensorEntity.altitude,
            speed = sensorEntity.speed,
            accuracy = sensorEntity.accuracy
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(locationEntity: LocationEntity)

    @Query("""
        SELECT 
            (timestamp / 600000) * 600000 AS bucketStart,
            COUNT(*) AS count
        FROM location
        WHERE bucketStart BETWEEN :startTime and :endTime
        GROUP BY bucketStart
        ORDER BY bucketStart
    """)
    override suspend fun getSummaryBetween(startTime: Long, endTime: Long): List<LocationSummary>
}