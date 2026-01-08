package kaist.iclab.wearabletracker.db.dao

import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.wearabletracker.db.entity.CsvSerializable

interface BaseDao<T: SensorEntity> {
    suspend fun insert(sensorEntity: T)
    suspend fun deleteAll()
    
    /**
     * Get all data for CSV export.
     * Returns a list of CsvSerializable entities.
     */
    suspend fun getAllForExport(): List<CsvSerializable>
}