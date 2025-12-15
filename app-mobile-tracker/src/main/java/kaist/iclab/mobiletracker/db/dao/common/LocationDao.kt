package kaist.iclab.mobiletracker.db.dao.common

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.data.DeviceType
import kaist.iclab.mobiletracker.db.entity.LocationEntity
import kaist.iclab.tracker.sensor.common.LocationSensor

/**
 * Unified DAO for location sensor data from both phone and watch devices.
 * Supports both:
 * - Phone sensors: Converts from LocationSensor.Entity to LocationEntity
 * - Watch sensors: Works directly with LocationEntity (from CSV parsing)
 */
@Dao
interface LocationDao: BaseDao<LocationSensor.Entity, LocationEntity> {
    
    // Methods for phone sensors (converting from LocationSensor.Entity)
    override suspend fun insert(sensorEntity: LocationSensor.Entity, userUuid: String?) {
        val entity = LocationEntity(
            uuid = userUuid ?: "",
            deviceType = DeviceType.PHONE.value,
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

    override suspend fun insertBatch(entities: List<LocationSensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            LocationEntity(
                uuid = userUuid ?: "",
                deviceType = DeviceType.PHONE.value,
                received = entity.received,
                timestamp = entity.timestamp,
                latitude = entity.latitude,
                longitude = entity.longitude,
                altitude = entity.altitude,
                speed = entity.speed,
                accuracy = entity.accuracy
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    // Methods for watch sensors (working directly with LocationEntity)
    @Insert
    suspend fun insertUsingRoomEntity(locationEntity: LocationEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<LocationEntity>)

    // Convenience method for watch sensors to insert a list directly
    suspend fun insertLocationEntities(entities: List<LocationEntity>) {
        if (entities.isNotEmpty()) {
            insertBatchUsingRoomEntity(entities)
        }
    }

    // Query methods
    @Query("SELECT * FROM location ORDER BY timestamp ASC")
    suspend fun getAllLocationData(): List<LocationEntity>

    @Query("SELECT * FROM location WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<LocationEntity>

    // Device-specific query methods
    @Query("SELECT * FROM location WHERE deviceType = :deviceType AND timestamp > :afterTimestamp ORDER BY timestamp ASC")
    suspend fun getDataAfterTimestampByDeviceType(afterTimestamp: Long, deviceType: Int): List<LocationEntity>

    @Query("SELECT MAX(timestamp) FROM location")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM location")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM location")
    override suspend fun deleteAll()
}
