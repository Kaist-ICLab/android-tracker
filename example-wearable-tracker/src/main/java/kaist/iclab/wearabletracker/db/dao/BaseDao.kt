package kaist.iclab.wearabletracker.db.dao

import kaist.iclab.tracker.sensor.core.SensorEntity

interface BaseDao<T: SensorEntity> {
    suspend fun insert(sensorEntity: T)
}