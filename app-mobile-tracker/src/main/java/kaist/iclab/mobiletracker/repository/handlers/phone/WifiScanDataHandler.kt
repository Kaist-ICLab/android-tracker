package kaist.iclab.mobiletracker.repository.handlers.phone

import kaist.iclab.mobiletracker.db.dao.phone.WifiScanDao
import kaist.iclab.mobiletracker.repository.SensorRecord
import kaist.iclab.mobiletracker.repository.handlers.SensorDataHandler

/**
 * Handler for WiFi Scan sensor data.
 */
class WifiScanDataHandler(private val dao: WifiScanDao) : SensorDataHandler {
    override val sensorId = "WifiScan"
    override val displayName = "WiFi Scan"
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
                    "SSID" to entity.ssid,
                    "BSSID" to entity.bssid
                )
            )
        }

    override suspend fun deleteAll() = dao.deleteAll()
    override suspend fun deleteById(id: Long) = dao.deleteById(id)
}
