package kaist.iclab.mobiletracker.db.dao

import kaist.iclab.tracker.sensor.core.SensorEntity

interface BaseDao<T: SensorEntity> {
    suspend fun insert(sensorEntity: T)
    suspend fun deleteAll()
}
