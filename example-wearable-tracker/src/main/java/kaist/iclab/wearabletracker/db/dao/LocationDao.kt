package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import kaist.iclab.tracker.sensor.common.LocationSensor
import kaist.iclab.wearabletracker.db.entity.LocationEntity

@Dao
interface LocationDao: BaseDao<LocationSensor.Entity> {
    override suspend fun insert(entity: LocationSensor.Entity) {
        val entity = LocationEntity(
            received = entity.received,
            timestamp = entity.timestamp,
            latitude = entity.latitude,
            longitude = entity.longitude,
            altitude = entity.altitude,
            speed = entity.speed,
            accuracy = entity.accuracy
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(locationEntity: LocationEntity)
}