package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.common.LocationSensor
import kaist.iclab.wearabletracker.db.entity.LocationEntity

@Dao
interface LocationDao: BaseDao<LocationSensor.Entity> {
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
}