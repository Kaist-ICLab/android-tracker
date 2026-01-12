package kaist.iclab.mobiletracker.repository.handlers.phone

import kaist.iclab.mobiletracker.db.dao.phone.AmbientLightDao
import kaist.iclab.mobiletracker.repository.SensorRecord
import kaist.iclab.mobiletracker.repository.handlers.SensorDataHandler

/**
 * Handler for Ambient Light sensor data.
 */
class AmbientLightDataHandler(private val dao: AmbientLightDao) : SensorDataHandler {
    override val sensorId = "AmbientLight"
    override val displayName = "Ambient Light"
    override val isWatchSensor = false

    override suspend fun getRecordCount() = dao.getRecordCount()
    override suspend fun getLatestTimestamp() = dao.getLatestTimestamp()
    override suspend fun getRecordCountAfterTimestamp(timestamp: Long) = 
        dao.getRecordCountAfterTimestamp(timestamp)

    override suspend fun getRecordsPaginated(
        afterTimestamp: Long,
        isAscending: Boolean,
        limit: Int,
        offset: Int
    ): List<SensorRecord> = dao.getRecordsPaginated(afterTimestamp, isAscending, limit, offset)
        .map { entity ->
            SensorRecord(
                id = entity.id.toLong(),
                timestamp = entity.timestamp,
                fields = mapOf(
                    "Value" to String.format("%.1f lux", entity.value)
                )
            )
        }

    override suspend fun deleteAll() = dao.deleteAll()
    override suspend fun deleteById(id: Long) = dao.deleteById(id)
}
