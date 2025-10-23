package kaist.iclab.wearabletracker.db.dao

import kaist.iclab.tracker.sensor.core.SensorEntity
import java.io.Serializable

interface BaseDao<T: SensorEntity, R: Serializable> {
    suspend fun insert(sensorEntity: T)

    suspend fun getSummaryBetween(startTime: Long, endTime: Long): List<R>
}